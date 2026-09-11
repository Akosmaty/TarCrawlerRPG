package com.tarotcrawler.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Czar: obrazenia i/lub leczenie i/lub buff. Sygnatury klas + zwoje z lootu. */
public class Spell {

    public enum Target { FOE, ALLY, ALL_ALLIES, SELF }

    public final String id;
    public final String name;
    public final int cost;
    public final int tier;
    public final double mult;
    public final Target target;
    public final int healBase;
    public final int healPerLvl;
    public final int shieldDef;
    public final int shieldRounds;
    public final int dodgePct;
    public final int dodgeRounds;
    public final int selfDodge;
    public final int selfDodgeRounds;

    private static final Map<String, Spell> REG = new LinkedHashMap<>();

    public static final Spell ISKRA = reg(new Spell("ISKRA", "Iskra", 5, 0,
            1.2, Target.FOE, 0, 0, 0, 0, 0, 0, 0, 0));
    public static final Spell PIORUN = reg(new Spell("PIORUN", "Piorun Arkanow", 10, 1,
            1.6, Target.FOE, 0, 0, 0, 0, 0, 0, 0, 0));
    public static final Spell KSIEZYC = reg(new Spell("KSIEZYC", "Ksiezycowe Ostrze", 8, 1,
            1.4, Target.FOE, 0, 0, 0, 0, 0, 0, 0, 0));
    public static final Spell SAD = reg(new Spell("SAD", "Ognisty Sad", 16, 3,
            2.0, Target.FOE, 0, 0, 0, 0, 0, 0, 0, 0));
    public static final Spell WIEZA = reg(new Spell("WIEZA", "Gniew Wiezy", 22, 5,
            2.4, Target.FOE, 0, 0, 0, 0, 0, 0, 0, 0));
    public static final Spell KAMIEN = reg(new Spell("KAMIEN", "Kamienna Skora", 10, 2,
            0.0, Target.SELF, 0, 0, 8, 3, 0, 0, 0, 0));

    public static final Spell FIKOLEK = reg(new Spell("FIKOLEK", "Fikolek Glupca", 8, 0,
            0.0, Target.SELF, 0, 0, 0, 0, 35, 2, 0, 0));
    public static final Spell KULA_OGNIA = reg(new Spell("KULA_OGNIA", "Kula Ognia", 12, 0,
            1.9, Target.FOE, 0, 0, 0, 0, 0, 0, 0, 0));
    public static final Spell BASTION = reg(new Spell("BASTION", "Bastion Cesarza", 12, 0,
            0.0, Target.ALL_ALLIES, 0, 0, 8, 3, 0, 0, 0, 0));
    public static final Spell MODLITWA = reg(new Spell("MODLITWA", "Modlitwa", 10, 0,
            0.0, Target.ALLY, 40, 8, 0, 0, 0, 0, 0, 0));
    public static final Spell BLOGOSLAWIENSTWO = reg(new Spell("BLOGOSLAWIENSTWO", "Blogoslawienstwo", 14, 0,
            0.0, Target.ALL_ALLIES, 20, 4, 0, 0, 0, 0, 0, 0));
    public static final Spell WIEZ_SERC = reg(new Spell("WIEZ_SERC", "Wiez Serc", 14, 0,
            0.0, Target.ALLY, 55, 10, 0, 0, 0, 0, 0, 0));
    public static final Spell SZARZA = reg(new Spell("SZARZA", "Szarza Rydwanu", 16, 0,
            2.2, Target.FOE, 0, 0, 0, 0, 0, 0, 0, 0));
    public static final Spell WYROK = reg(new Spell("WYROK", "Wyrok", 14, 0,
            1.8, Target.FOE, 0, 0, 0, 0, 0, 0, 0, 0));
    public static final Spell LATARNIA = reg(new Spell("LATARNIA", "Latarnia Prawdy", 12, 0,
            1.3, Target.FOE, 0, 0, 0, 0, 0, 0, 25, 2));

    private Spell(String id, String name, int cost, int tier, double mult, Target target,
                  int healBase, int healPerLvl, int shieldDef, int shieldRounds,
                  int dodgePct, int dodgeRounds, int selfDodge, int selfDodgeRounds) {
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.tier = tier;
        this.mult = mult;
        this.target = target;
        this.healBase = healBase;
        this.healPerLvl = healPerLvl;
        this.shieldDef = shieldDef;
        this.shieldRounds = shieldRounds;
        this.dodgePct = dodgePct;
        this.dodgeRounds = dodgeRounds;
        this.selfDodge = selfDodge;
        this.selfDodgeRounds = selfDodgeRounds;
    }

    private static Spell reg(Spell s) {
        REG.put(s.id, s);
        return s;
    }

    public static Spell byId(String id) {
        return REG.get(id);
    }

    public String shortDesc() {
        List<String> parts = new ArrayList<>();
        if (mult > 0) parts.add("x" + mult + " dmg");
        if (healBase > 0) parts.add("leczy " + healBase + "+");
        if (shieldDef > 0) parts.add("tarcza +" + shieldDef + " DEF");
        if (dodgePct > 0) parts.add("unik +" + dodgePct + "%");
        if (selfDodge > 0) parts.add("sobie unik +" + selfDodge + "%");
        return name + " (" + cost + " many" + (parts.isEmpty() ? "" : ": " + String.join(", ", parts)) + ")";
    }
}
