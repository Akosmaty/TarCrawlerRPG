package com.tarotcrawler.model;

import com.tarotcrawler.TestRunner;

public class SpellRegistryTest {

    public void testSygnaturyWszystkichKlas() {
        for (HeroClass hc : HeroClass.values()) {
            Spell s = Spell.byId(hc.getSignatureSpell());
            TestRunner.ok(s != null, hc + " ma sygnature");
        }
    }

    public void testLootoweIstnieja() {
        for (String id : new String[]{"ISKRA", "PIORUN", "KSIEZYC", "KAMIEN", "SAD", "WIEZA"}) {
            Spell s = Spell.byId(id);
            TestRunner.ok(s != null && s.mult >= 0 && s.cost > 0, id + " ok");
        }
    }

    public void testNaukaTylkoRaz() {
        Hero h = new Hero(HeroClass.MAG);
        TestRunner.ok(h.learnSpell("PIORUN"), "pierwsza nauka");
        TestRunner.ok(!h.learnSpell("PIORUN"), "dubel odrzucony");
        TestRunner.ok(!h.learnSpell("KULA_OGNIA"), "sygnatury nie doucza");
    }
}
