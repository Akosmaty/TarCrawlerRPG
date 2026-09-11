package com.tarotcrawler.model;

import java.util.Random;

/** Tabela wrogow-kart dla lochu. */
public final class EnemyTable {

    private static final String[] ROMAN = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};

    private EnemyTable() {}

    public static String suitName(Dungeon d) {
        switch (d) {
            case KIELICHY: return "Kielichow";
            case MIECZE: return "Mieczy";
            case MONETY: return "Monet";
            case BULAWY: return "Bulaw";
            default: return d.name();
        }
    }

    /** Pietra 1-7: karty liczbowe I-X, numer rosnie z pietrem. */
    public static Enemy pipFor(Dungeon d, int floor, Random rng) {
        int idx = Math.min(9, Math.max(0, floor - 1 + rng.nextInt(2)));
        int n = idx + 1;
        String name = ROMAN[idx] + " " + suitName(d);
        int hp = 25 + n * 10 + floor * 5;
        int atk = 4 + n + floor;
        int exp = 20 + floor * 10 + n * 2;
        int spd = 3 + floor / 2;
        int def = 1 + floor / 3;
        return new Enemy("PIP", name, hp, atk, spd, def, 0, exp, false, "");
    }

    /** Pietra 8-9: figury - Jopek (Paz), Rycerz, Krolowa, Krol, As. */
    public static Enemy courtFor(Dungeon d, int floor, Random rng) {
        String[] ranks = {"Jopek", "Rycerz", "Krolowa", "Krol", "As"};
        String[] specs = {
            "Szybki: bije pierwszy",
            "Szarza: wysoki atak",
            "Rozkaz: wzywa {F} I na pomoc (prototyp: +HP)",
            "Ciezka zbroja: wysokie HP",
            "Esencja koloru: mocny atak + exp"
        };
        int i = rng.nextInt(ranks.length);
        String name = ranks[i] + " " + suitName(d);
        int hp = 120 + floor * 15 + i * 10;
        int atk = 14 + floor * 2 + i;
        int exp = 90 + floor * 12 + i * 5;
        int spd = 5 + i;
        int def = 5 + i;
        int mag = 2 + i;
        return new Enemy("COURT", name, hp, atk, spd, def, mag, exp, false, specs[i]);
    }

    /** Pietro 10: boss losowany z duzych arkan V-IX. */
    public static Enemy randomBoss(Random rng) {
        Enemy[] pool = {
            new Enemy("PAPIEZ", "V - Papiez", 220, 22, 5, 12, 8, 300, true, "Blogoslawienstwo: boss leczy 20 HP co ture"),
            new Enemy("KOCHANKOWIE", "VI - Kochankowie", 200, 24, 7, 10, 7, 300, true, "Wybor: co ture losuje slaby albo silny atak"),
            new Enemy("RYDWAN", "VII - Rydwan", 240, 26, 9, 14, 5, 320, true, "Szarza: zawsze bije pierwszy"),
            new Enemy("SPRAWIEDLIWOSC", "VIII - Sprawiedliwosc", 230, 25, 6, 13, 9, 320, true, "Waga: rowna obrazenia w obie strony co 3 ture"),
            new Enemy("PUSTELNIK", "IX - Pustelnik", 210, 23, 6, 9, 10, 310, true, "Latarnia: widzi twoj ruch, wysoki crit")
        };
        return pool[rng.nextInt(pool.length)];
    }

    /** Dowolny wrog na dane pietro (bez bossa). */
    public static Enemy forFloor(Dungeon d, int floor, Random rng) {
        if (floor >= 8) {
            return courtFor(d, floor, rng);
        }
        return pipFor(d, floor, rng);
    }

    /** Skalowanie wroga wielkoscia druzyny: wiecej HP/ATK i wiecej expa. */
    public static Enemy scale(Enemy e, int partySize) {
        if (partySize <= 1) {
            return e;
        }
        double hpM = 1 + 0.7 * (partySize - 1);
        double atkM = 1 + 0.35 * (partySize - 1);
        double expM = 1 + 0.5 * (partySize - 1);
        return new Enemy(e.getId(), e.getName(),
                (int) (e.getHp() * hpM), (int) (e.getAtk() * atkM), e.getSpd(), e.getDef(), e.getMag(),
                (int) (e.getExp() * expM), e.isBoss(), e.getSpecial());
    }
}
