package com.tarotcrawler.model;

import com.tarotcrawler.TestRunner;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class EnemyTableTest {

    public void testPipRosnieZPietrem() {
        Random r = new Random(11);
        Enemy slaby = EnemyTable.pipFor(Dungeon.MIECZE, 1, r);
        Enemy mocny = EnemyTable.pipFor(Dungeon.MIECZE, 7, r);
        TestRunner.ok(mocny.getHp() > slaby.getHp(), "hp rosnie");
        TestRunner.ok(mocny.getAtk() > slaby.getAtk(), "atk rosnie");
        TestRunner.ok(mocny.getExp() > slaby.getExp(), "exp rosnie");
    }

    public void testFiguryNa89() {
        Random r = new Random(12);
        Set<String> nazwy = new HashSet<>();
        for (int i = 0; i < 20; i++) {
            Enemy e = EnemyTable.courtFor(Dungeon.KIELICHY, 8, r);
            TestRunner.ok(!e.isBoss(), "figura nie boss");
            nazwy.add(e.getName().split(" ")[0]);
        }
        TestRunner.ok(nazwy.contains("Rycerz"), "sa rycerze: " + nazwy);
        TestRunner.ok(nazwy.contains("Krol"), "sa krolowie: " + nazwy);
    }

    public void testBossowieZVINaIX() {
        Random r = new Random(13);
        Set<String> id = new HashSet<>();
        for (int i = 0; i < 30; i++) {
            Enemy b = EnemyTable.randomBoss(r);
            TestRunner.ok(b.isBoss(), "boss flaga");
            TestRunner.ok(b.getDef() > 0 && b.getMag() > 0, "boss ma DEF/MAG");
            id.add(b.getId());
        }
        TestRunner.eq(5, id.size(), "pula 5 bossow");
        TestRunner.ok(id.contains("RYDWAN"), "jest Rydwan");
    }

    public void testSkalowanieDruzyna() {
        Random r = new Random(14);
        Enemy baza = EnemyTable.pipFor(Dungeon.MONETY, 3, r);
        Enemy solo = EnemyTable.scale(baza, 1);
        TestRunner.eq(baza.getHp(), solo.getHp(), "solo bez zmian");
        Enemy party = EnemyTable.scale(baza, 4);
        TestRunner.ok(party.getHp() > baza.getHp(), "wieksze hp dla party");
        TestRunner.ok(party.getAtk() > baza.getAtk(), "wiekszy atk dla party");
        TestRunner.ok(party.getExp() > baza.getExp(), "wiekszy exp dla party");
        TestRunner.eq(baza.getSpd(), party.getSpd(), "spd bez zmian");
    }
}
