package com.tarotcrawler.model;

import com.tarotcrawler.TestRunner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RunStateTest {

    public void testAgregacjaDruzyny() {
        List<Hero> party = Arrays.asList(new Hero(HeroClass.GLUPIEC), new Hero(HeroClass.MAG));
        RunState s = new RunState(party, Dungeon.MIECZE);
        TestRunner.eq(8 + 14, s.power(), "moc = suma ATK (lvl daje punkty, nie moc)");
        TestRunner.eq(80 + 70, s.maxHp(), "hp = suma");
        TestRunner.eq((5 + 3) / 2, s.defense(), "def = srednia");
        TestRunner.eq("GLUPIEC", s.getLeader().getTemplate().name(), "lider pierwszy");
    }

    public void testKontraKoloru() {
        RunState s = new RunState(
                Collections.singletonList(new Hero(HeroClass.MAG)), Dungeon.KIELICHY);
        TestRunner.ok(s.counters(Dungeon.KIELICHY), "mag kontruje kielichy");
        TestRunner.ok(!s.counters(Dungeon.MIECZE), "mag nie kontruje mieczy");
    }

    public void testWalidacjaDruzyny() {
        boolean pusta = false;
        try {
            new RunState(new ArrayList<Hero>(), Dungeon.MIECZE);
        } catch (IllegalArgumentException e) {
            pusta = true;
        }
        TestRunner.ok(pusta, "pusta druzyna rzuca");
    }
}
