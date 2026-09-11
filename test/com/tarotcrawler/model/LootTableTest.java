package com.tarotcrawler.model;

import com.tarotcrawler.TestRunner;
import java.util.Random;

public class LootTableTest {

    public void testKsztaltDropu() {
        Random r = new Random(21);
        Enemy e = EnemyTable.pipFor(Dungeon.MIECZE, 3, r);
        LootTable.Item[] drop = LootTable.roll(e, Dungeon.MIECZE, r);
        TestRunner.eq(3, drop.length, "3 propozycje");
        int wands = 0;
        for (LootTable.Item i : drop) {
            if (i.gear.isWand()) {
                wands++;
            }
        }
        TestRunner.eq(1, wands, "dokladnie 1 rozdzka");
    }

    public void testBossDajeLepszyLoot() {
        Random r = new Random(22);
        Enemy slaby = EnemyTable.pipFor(Dungeon.MONETY, 1, r);
        Enemy boss = EnemyTable.randomBoss(r);
        int slabyAtk = 0;
        for (LootTable.Item i : LootTable.roll(slaby, Dungeon.MONETY, r)) {
            slabyAtk = Math.max(slabyAtk, i.gear.atk);
        }
        int bossAtk = 0;
        for (LootTable.Item i : LootTable.roll(boss, Dungeon.MONETY, r)) {
            bossAtk = Math.max(bossAtk, i.gear.atk);
        }
        TestRunner.ok(bossAtk >= slabyAtk, "boss loot >= slaby (" + bossAtk + " vs " + slabyAtk + ")");
    }

    public void testRozdzkaMaCzar() {
        Random r = new Random(23);
        Enemy boss = EnemyTable.randomBoss(r);
        LootTable.Item wand = LootTable.wandFor(boss, Dungeon.BULAWY, r);
        TestRunner.ok(wand.gear.isWand(), "to rozdzka");
        TestRunner.ok(Spell.byId(wand.gear.spellId) != null, "czar istnieje");
        TestRunner.ok(wand.describe().contains("czar:"), "opis ma czar: " + wand.describe());
    }
}
