package com.tarotcrawler.model;

public enum HeroClass {
    GLUPIEC("0 - Glupiec", "starter: Loch Monet",
            80, 8, 5, 20, 8,
            "Unik 20%, ucieczka zawsze dziala, bonus do skrzyn.",
            "MONETY", "\u25A0", true, "FIKOLEK"),
    MAG("I - Mag", "starter: Loch Kielichow",
            70, 14, 3, 60, 5,
            "Firebolt x2 za mane. Burst zabija zanim wrog sie uleczy.",
            "KIELICHY", "\u25CF", true, "KULA_OGNIA"),
    CESARZ("IV - Cesarz", "loch Mieczy: wyczysc go aby odblokowac",
            130, 10, 15, 20, 3,
            "Tank: +15 DEF, tarcza Bastion na ataki fizyczne.",
            "MIECZE", "\u271A", false, "BASTION"),
    KAPLANKA("II - Kaplanka", "loch Bulaw: wyczysc go aby odblokowac",
            90, 9, 7, 50, 4,
            "Tarcza blokuje DoT, maly heal. Dobra na przeczekanie ognia.",
            "BULAWY", "\u25B2", false, "MODLITWA"),
    PAPIEZ("V - Papiez", "boss V: pokonaj go aby odblokowac",
            120, 12, 12, 60, 4,
            "Tarcza wiary: wysokie HP i DEF, dobry na Kielichy.",
            "KIELICHY", "\u2663", false, "BLOGOSLAWIENSTWO"),
    KOCHANKOWIE("VI - Kochankowie", "boss VI: pokonaj go aby odblokowac",
            100, 15, 8, 50, 6,
            "Podwojny atak serca: wysoki ATK, dobrzy na Monety.",
            "MONETY", "\u2665", false, "WIEZ_SERC"),
    RYDWAN("VII - Rydwan", "boss VII: pokonaj go aby odblokowac",
            140, 14, 10, 30, 7,
            "Szarza: wysoki ATK i HP, dobry na Miecze.",
            "MIECZE", "\u2660", false, "SZARZA"),
    SPRAWIEDLIWOSC("VIII - Sprawiedliwosc", "boss VIII: pokonaj go aby odblokowac",
            115, 13, 13, 40, 5,
            "Waga: zrownowazone staty, dobra na Bulawy.",
            "BULAWY", "\u2666", false, "WYROK"),
    PUSTELNIK("IX - Pustelnik", "boss IX: pokonaj go aby odblokowac",
            95, 16, 6, 70, 5,
            "Latarnia: najwyzszy ATK, papierowy. Dobry na Miecze.",
            "MIECZE", "\u2605", false, "LATARNIA");

    private final String name;
    private final String role;
    private final int hp;
    private final int atk;
    private final int def;
    private final int mana;
    private final int spd;
    private final String passive;
    private final String strongAgainst;
    private final String symbol;
    private final boolean starter;
    private final String signatureSpell;

    HeroClass(String name, String role, int hp, int atk, int def, int mana, int spd,
              String passive, String strongAgainst, String symbol, boolean starter,
              String signatureSpell) {
        this.name = name;
        this.role = role;
        this.hp = hp;
        this.atk = atk;
        this.def = def;
        this.mana = mana;
        this.spd = spd;
        this.passive = passive;
        this.strongAgainst = strongAgainst;
        this.symbol = symbol;
        this.starter = starter;
        this.signatureSpell = signatureSpell;
    }

    public String getName() { return name; }
    public String getRole() { return role; }
    public int getHp() { return hp; }
    public int getAtk() { return atk; }
    public int getDef() { return def; }
    public int getMana() { return mana; }
    public int getSpd() { return spd; }
    public String getSignatureSpell() { return signatureSpell; }
    public String getPassive() { return passive; }
    public String getStrongAgainst() { return strongAgainst; }
    public String getSymbol() { return symbol; }
    public boolean isStarter() { return starter; }
}
