package com.tarotcrawler.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** Loot z wrogow: 2 sztuki ekwipunku (6 slotow) + 1 rozdzka. Wartosc rosnie z sila wroga. */
public final class LootTable {

    public static class Item {
        public final String name;
        public final Gear gear;

        public Item(Gear gear) {
            this.gear = gear;
            this.name = gear.name;
        }

        public String describe() {
            return gear.describe();
        }
    }

    private LootTable() {}

    /** Rozdzka do reki dajaca czar w walce - im mocniejszy wrog, tym lepszy czar. */
    public static Item wandFor(Enemy foe, Dungeon d, Random rng) {
        int tier = Math.max(1, foe.getExp() / 50);
        String suit = EnemyTable.suitName(d);
        List<Spell> pool = new ArrayList<>();
        pool.add(Spell.PIORUN);
        pool.add(Spell.KSIEZYC);
        if (tier >= 2) {
            pool.add(Spell.KAMIEN);
        }
        if (tier >= 3) {
            pool.add(Spell.SAD);
        }
        if (tier >= 5) {
            pool.add(Spell.WIEZA);
        }
        Spell s = pool.get(rng.nextInt(pool.size()));
        String[] kind = {"Rozdzka", "Kostur", "Berlo"};
        String name = kind[rng.nextInt(kind.length)] + " " + s.name + " " + suit;
        Gear.Slot hand = rng.nextBoolean() ? Gear.Slot.HAND_L : Gear.Slot.HAND_R;
        return new Item(new Gear(name, hand, s.id, tier, 4 * tier));
    }

    /** Losowa sztuka ekwipunku na dany slot, skalowana tierem wroga. */
    public static Item gearFor(Gear.Slot slot, Dungeon d, int tier, Random rng) {
        String suit = EnemyTable.suitName(d);
        switch (slot) {
            case HELM:
                return new Item(new Gear("Helm " + suit, slot,
                        0, 2 * tier + rng.nextInt(tier + 1), 4 * tier + rng.nextInt(2 * tier + 1),
                        0, 0, 0));
            case ARMOR:
                return new Item(new Gear("Zbroja " + suit, slot,
                        0, 3 * tier + rng.nextInt(tier + 1), 6 * tier + rng.nextInt(3 * tier + 1),
                        0, 0, 0));
            case BOOTS:
                return new Item(new Gear("Buty " + suit, slot,
                        0, tier, 2 * tier, 1 + tier / 2, 0, 3));
            case HAND_L:
            case HAND_R:
                if (rng.nextBoolean()) {
                    return new Item(new Gear("Miecz " + suit, slot,
                            2 * tier + rng.nextInt(tier + 1), 0, 0, tier / 2, 0, 0));
                }
                return new Item(new Gear("Tarcza " + suit, slot,
                        0, 3 * tier + rng.nextInt(tier + 1), 2 * tier, 0, 0, 0));
            case JEWELRY:
            default:
                return new Item(new Gear("Amulet " + suit, slot,
                        tier, tier, 4 * tier, 1, 6 * tier, 4));
        }
    }

    /** 2 rozne sloty + 1 rozdzka do wyboru. */
    public static Item[] roll(Enemy foe, Dungeon d, Random rng) {
        int tier = Math.max(1, foe.getExp() / 50);
        List<Gear.Slot> slots = new ArrayList<>();
        Collections.addAll(slots, Gear.Slot.values());
        Collections.shuffle(slots, rng);
        return new Item[]{
                gearFor(slots.get(0), d, tier, rng),
                gearFor(slots.get(1), d, tier, rng),
                wandFor(foe, d, rng)
        };
    }
}
