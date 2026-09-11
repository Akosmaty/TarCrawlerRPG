package com.tarotcrawler.model;

import com.tarotcrawler.TestRunner;

public class HeroAttrTest {

    public void testPoziomDajePule() {
        Hero h = new Hero(HeroClass.MAG);
        TestRunner.eq(0, h.getPool(), "start pool");
        int ups = h.addExp(500);
        TestRunner.ok(ups >= 1, "awans ma nastapic");
        TestRunner.eq(ups * Hero.POINTS_PER_LEVEL, h.getPool(), "pula za awanse");
    }

    public void testRozdanieStatystyk() {
        Hero h = new Hero(HeroClass.MAG);
        int moc0 = h.power();
        int hp0 = h.maxHp();
        h.addPool(10);
        TestRunner.ok(h.spendPoint(Hero.Attr.STR), "STR");
        TestRunner.ok(h.spendPoint(Hero.Attr.HP), "HP");
        TestRunner.ok(h.spendPoint(Hero.Attr.MAG), "MAG");
        TestRunner.eq(1, h.attrValue(Hero.Attr.STR), "STR=1");
        TestRunner.eq(moc0 + 2, h.power(), "moc +2 za STR");
        TestRunner.eq(hp0 + 10, h.maxHp(), "hp +10 za Zycie");
        TestRunner.ok(Math.abs(h.spellMult() - 1.1) < 0.001, "magia +10%");
        TestRunner.eq(7, h.getPool(), "zostalo 7");
    }

    public void testBrakPunktow() {
        Hero h = new Hero(HeroClass.GLUPIEC);
        TestRunner.ok(!h.spendPoint(Hero.Attr.STR), "bez punktow nie da sie");
        TestRunner.eq(0, h.attrValue(Hero.Attr.STR), "STR dalej 0");
    }

    public void testUnikISzybkosc() {
        Hero h = new Hero(HeroClass.GLUPIEC);
        int spd0 = h.speed();
        h.addPool(4);
        h.spendPoint(Hero.Attr.SPD);
        h.spendPoint(Hero.Attr.DODGE);
        TestRunner.eq(spd0 + 1, h.speed(), "spd +1");
        TestRunner.eq(6, h.dodge(), "unik 5+1");
    }
}
