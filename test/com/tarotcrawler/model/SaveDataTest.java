package com.tarotcrawler.model;

import com.tarotcrawler.TestRunner;
import java.util.Arrays;

public class SaveDataTest {

    public void testRoundTripZPula() {
        SaveData d = SaveData.create();
        d.unlocked.add("RYDWAN");
        d.cleared.add(Dungeon.MIECZE);
        d.party.add("MAG");
        d.heroes.put("MAG", new int[]{3, 40, 7});
        d.spells.put("MAG", Arrays.asList("ISKRA", "PIORUN"));
        d.save();

        SaveData back = SaveData.load();
        TestRunner.ok(back.unlocked.contains("RYDWAN"), "unlock wrocil");
        TestRunner.ok(back.cleared.contains(Dungeon.MIECZE), "loch wrocil");
        TestRunner.eq(1, back.party.size(), "druzyna wrocila");
        int[] hero = back.heroes.get("MAG");
        TestRunner.eq(3, hero[0], "lvl");
        TestRunner.eq(40, hero[1], "exp");
        TestRunner.eq(7, hero[2], "pula");
        TestRunner.eq(2, back.spells.get("MAG").size(), "czary");
    }
}
