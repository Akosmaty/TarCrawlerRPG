package com.tarotcrawler.model;

/** Element ekwipunku. Slot + bonusy. Sprzet jest tylko na run. */
public class Gear {

    public enum Slot {
        HELM("Helm"), ARMOR("Zbroja"), BOOTS("Buty"),
        HAND_L("Lewa reka"), HAND_R("Prawa reka"), JEWELRY("Bizuteria");

        private final String label;

        Slot(String label) {
            this.label = label;
        }

        public String getLabel() { return label; }
    }

    public final String name;
    public final Slot slot;
    public final int atk;
    public final int def;
    public final int hp;
    public final int spd;
    public final int mana;
    public final int dodge;
    public final String spellId;

    public Gear(String name, Slot slot, int atk, int def, int hp, int spd, int mana, int dodge) {
        this(name, slot, atk, def, hp, spd, mana, dodge, null);
    }

    /** Bron-czar: rozdzka/kostur do reki dajacy czar w walce. */
    public Gear(String name, Slot slot, String spellId, int atk, int mana) {
        this(name, slot, atk, 0, 0, 0, mana, 0, spellId);
    }

    private Gear(String name, Slot slot, int atk, int def, int hp, int spd, int mana, int dodge,
                 String spellId) {
        this.name = name;
        this.slot = slot;
        this.atk = atk;
        this.def = def;
        this.hp = hp;
        this.spd = spd;
        this.mana = mana;
        this.dodge = dodge;
        this.spellId = spellId;
    }

    public boolean isWand() { return spellId != null; }

    public String slotLabel() {
        if (slot == Slot.HAND_L || slot == Slot.HAND_R) {
            return "Reka";
        }
        return slot.getLabel();
    }

    public String bonusText() {
        StringBuilder sb = new StringBuilder();
        if (atk > 0) sb.append("+").append(atk).append(" ATK ");
        if (def > 0) sb.append("+").append(def).append(" DEF ");
        if (hp > 0) sb.append("+").append(hp).append(" HP ");
        if (spd > 0) sb.append("+").append(spd).append(" SPD ");
        if (mana > 0) sb.append("+").append(mana).append(" MANA ");
        if (dodge > 0) sb.append("+").append(dodge).append("% UNIK ");
        return sb.toString().trim();
    }

    public String describe() {
        StringBuilder sb = new StringBuilder(name + " [" + slotLabel() + "] (");
        if (isWand()) {
            Spell s = Spell.byId(spellId);
            sb.append("czar: ").append(s.name);
            if (s.mult > 0) {
                sb.append(" x").append(s.mult);
            }
            sb.append(", ").append(s.cost).append(" many");
        } else {
            sb.append(bonusText());
        }
        sb.append(")");
        return sb.toString();
    }
}
