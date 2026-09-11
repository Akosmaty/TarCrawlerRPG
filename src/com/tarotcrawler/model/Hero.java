package com.tarotcrawler.model;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** Konkretna postac: szablon + lvl/exp + pula punktow atrybutow + ekwipunek + czary. */
public class Hero {

    public enum Attr {
        STR("Sila", "+2 ATK"),
        SPD("Szybkosc", "+1 SPD"),
        MANA("Mana", "+6 MANA"),
        HP("Zycie", "+8 HP"),
        MAG("Moc magii", "+10% czarow"),
        DODGE("Unik", "+1%");

        private final String label;
        private final String desc;

        Attr(String label, String desc) {
            this.label = label;
            this.desc = desc;
        }

        public String getLabel() { return label; }
        public String getDesc() { return desc; }
    }

    public static final int POINTS_PER_LEVEL = 4;

    private final HeroClass template;
    private int level = 1;
    private int exp = 0;
    private int pool = 0;
    private int hpCur;
    private int str;
    private int spd;
    private int manaPts;
    private int hpPts;
    private int mag;
    private int dodgePts;
    private final Map<Gear.Slot, Gear> gear = new EnumMap<>(Gear.Slot.class);
    private final List<String> learned = new ArrayList<>();
    private int mana;

    public Hero(HeroClass template) {
        this.template = template;
        this.mana = maxMana();
        this.hpCur = maxHp();
    }

    public HeroClass getTemplate() { return template; }
    public int getLevel() { return level; }
    public int getExp() { return exp; }
    public int getPool() { return pool; }
    public void setLevel(int level) { this.level = Math.max(1, level); }
    public void setExp(int exp) { this.exp = Math.max(0, exp); }

    public void addPool(int n) {
        pool = Math.max(0, pool + n);
    }

    /** Rozdanie punktu atrybutu. Zwraca false gdy brak punktow. */
    public boolean spendPoint(Attr a) {
        if (pool <= 0) {
            return false;
        }
        pool--;
        switch (a) {
            case STR: str++; break;
            case SPD: spd++; break;
            case MANA: manaPts++; break;
            case HP: hpPts++; break;
            case MAG: mag++; break;
            case DODGE: dodgePts++; break;
        }
        return true;
    }

    public int attrValue(Attr a) {
        switch (a) {
            case STR: return str;
            case SPD: return spd;
            case MANA: return manaPts;
            case HP: return hpPts;
            case MAG: return mag;
            case DODGE: return dodgePts;
            default: return 0;
        }
    }

    /** Zaklada sprzet, zwraca zdjety (przepada) albo null.
     *  Rece nie dubluja sie: wolna reka, inaczej podmiana slabszego. */
    public Gear equip(Gear g) {
        if (g.slot == Gear.Slot.HAND_L || g.slot == Gear.Slot.HAND_R) {
            Gear l = gear.get(Gear.Slot.HAND_L);
            Gear r = gear.get(Gear.Slot.HAND_R);
            if (l == null) {
                return gear.put(Gear.Slot.HAND_L, g);
            }
            if (r == null) {
                return gear.put(Gear.Slot.HAND_R, g);
            }
            if (gearValue(l) <= gearValue(r)) {
                return gear.put(Gear.Slot.HAND_L, g);
            }
            return gear.put(Gear.Slot.HAND_R, g);
        }
        return gear.put(g.slot, g);
    }

    private static int gearValue(Gear g) {
        return g.atk + g.def + g.hp / 4 + g.spd * 2 + g.mana / 2 + g.dodge * 2;
    }

    /** W ktorym slocie lezy ten przedmiot (tozsamosc), albo null. */
    public Gear.Slot slotOf(Gear g) {
        for (Map.Entry<Gear.Slot, Gear> e : gear.entrySet()) {
            if (e.getValue() == g) {
                return e.getKey();
            }
        }
        return null;
    }

    /** Zdejmuje sprzet ze slotu (niszczy), zwraca zdjety albo null. */
    public Gear unequip(Gear.Slot slot) {
        return gear.remove(slot);
    }

    public Map<Gear.Slot, Gear> getGear() { return new EnumMap<>(gear); }

    /** Sprzet jest tylko na run - czysc po wyjsciu z lochu. */
    public void clearGear() {
        gear.clear();
    }

    /** Obciecie progresu expa po porazce (lvl i pula zostaja). */
    public void cutExpToPercent(int percent) {
        exp = exp * percent / 100;
    }

    private int gearAtk() { int s = 0; for (Gear g : gear.values()) s += g.atk; return s; }
    private int gearDef() { int s = 0; for (Gear g : gear.values()) s += g.def; return s; }
    private int gearHp() { int s = 0; for (Gear g : gear.values()) s += g.hp; return s; }
    private int gearSpd() { int s = 0; for (Gear g : gear.values()) s += g.spd; return s; }
    private int gearMana() { int s = 0; for (Gear g : gear.values()) s += g.mana; return s; }
    private int gearDodge() { int s = 0; for (Gear g : gear.values()) s += g.dodge; return s; }

    public int power() {
        return template.getAtk() + gearAtk() + str * 2;
    }

    public int defense() {
        return template.getDef() + gearDef();
    }

    public int maxHp() {
        return template.getHp() + gearHp() + hpPts * 10;
    }

    /** Biezace HP w runie (bitwy je zuzywaja, kapliczki odnawiaja). */
    public int getHpCur() { return hpCur; }

    public void setHpCur(int v) {
        hpCur = Math.max(0, Math.min(v, maxHp()));
    }

    public void healFull() {
        hpCur = maxHp();
    }

    /** Regeneracja X% max HP; wskrzesza tez poleglych. */
    public void healPercent(int pct) {
        int amount = maxHp() * pct / 100;
        if (hpCur <= 0) {
            hpCur = Math.min(amount, maxHp());
        } else {
            hpCur = Math.min(maxHp(), hpCur + amount);
        }
    }

    public int speed() {
        return template.getSpd() + gearSpd() + spd;
    }

    public int dodge() {
        return 5 + gearDodge() + dodgePts;
    }

    public double spellMult() {
        return 1.0 + mag * 0.10;
    }

    /** Sygnatura + czary z rozdzek w rekach (+ douczone ze starych zwojow). */
    public List<String> getSpells() {
        List<String> out = new ArrayList<>();
        String sig = template.getSignatureSpell();
        if (sig != null && Spell.byId(sig) != null) {
            out.add(sig);
        }
        for (Gear g : gear.values()) {
            if (g.isWand() && Spell.byId(g.spellId) != null && !out.contains(g.spellId)) {
                out.add(g.spellId);
            }
        }
        for (String id : learned) {
            if (!out.contains(id) && Spell.byId(id) != null) {
                out.add(id);
            }
        }
        return out;
    }

    /** Zwraca false jesli czar byl juz znany. */
    public boolean learnSpell(String id) {
        if (id == null || learned.contains(id) || Spell.byId(id) == null) {
            return false;
        }
        if (id.equals(template.getSignatureSpell())) {
            return false;
        }
        learned.add(id);
        return true;
    }

    public void setSpells(List<String> ids) {
        learned.clear();
        if (ids != null) {
            for (String id : ids) {
                if (id != null && Spell.byId(id) != null && !learned.contains(id)
                        && !id.equals(template.getSignatureSpell())) {
                    learned.add(id);
                }
            }
        }
    }

    public int maxMana() {
        return template.getMana() + gearMana() + manaPts * 6;
    }

    public int getMana() { return mana; }

    public void resetMana() { mana = maxMana(); }

    public boolean spendMana(int cost) {
        if (mana < cost) {
            return false;
        }
        mana -= cost;
        return true;
    }

    /** Zwraca liczbe awansow; kazdy daje POINTS_PER_LEVEL punktow. */
    public int addExp(int amount) {
        exp += amount;
        int ups = 0;
        while (exp >= RunState.nextLevelExp(level)) {
            exp -= RunState.nextLevelExp(level);
            level++;
            ups++;
        }
        pool += ups * POINTS_PER_LEVEL;
        return ups;
    }
}
