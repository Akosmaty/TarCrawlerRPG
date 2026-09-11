package com.tarotcrawler.model;

/** Przeciwnik-karta: I-X na pietrach 1-7, figury na 8-9, boss V-IX na 10. */
public class Enemy {
    private final String id;
    private final String name;
    private final int hp;
    private final int atk;
    private final int spd;
    private final int def;
    private final int mag;
    private final int exp;
    private final boolean boss;
    private final String special;

    public Enemy(String id, String name, int hp, int atk, int spd, int def, int mag,
                 int exp, boolean boss, String special) {
        this.id = id;
        this.name = name;
        this.hp = hp;
        this.atk = atk;
        this.spd = spd;
        this.def = def;
        this.mag = mag;
        this.exp = exp;
        this.boss = boss;
        this.special = special;
    }

    public String getId() { return id; }

    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getAtk() { return atk; }
    public int getSpd() { return spd; }
    public int getDef() { return def; }
    public int getMag() { return mag; }
    public int getExp() { return exp; }
    public boolean isBoss() { return boss; }
    public String getSpecial() { return special; }

    public int dodge() {
        return 5 + spd;
    }

    public String statsLine() {
        return "HP " + hp + " ATK " + atk + " DEF " + def + " SPD " + spd
                + " MAG " + mag + " UNIK " + dodge() + "%";
    }

    @Override
    public String toString() {
        return name + " (HP " + hp + ", ATK " + atk + ")";
    }
}
