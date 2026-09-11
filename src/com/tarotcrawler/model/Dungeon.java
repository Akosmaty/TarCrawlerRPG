package com.tarotcrawler.model;

public enum Dungeon {
    KIELICHY("Loch Kielichow", "Zalane piwnice. Wrogowie lecza sie co ture.",
            new java.awt.Color(70, 130, 180), "MAG"),
    MIECZE("Loch Mieczy", "Koszarowe korytarze. Silne ataki fizyczne.",
            new java.awt.Color(169, 169, 169), "CESARZ"),
    MONETY("Loch Monet", "Skarbiec. Wolne golemy-tanki, najlepszy loot.",
            new java.awt.Color(218, 165, 32), "GLUPIEC"),
    BULAWY("Loch Bulaw", "Kuznia / wulkan. Pola ognia i podpalenia (DoT).",
            new java.awt.Color(178, 34, 34), "KAPLANKA");

    private final String name;
    private final String description;
    private final java.awt.Color color;
    private final String counterClassId;

    Dungeon(String name, String description, java.awt.Color color, String counterClassId) {
        this.name = name;
        this.description = description;
        this.color = color;
        this.counterClassId = counterClassId;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public java.awt.Color getColor() { return color; }
    public String getCounterClassId() { return counterClassId; }
}
