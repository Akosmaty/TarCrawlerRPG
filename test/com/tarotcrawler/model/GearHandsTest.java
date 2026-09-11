package com.tarotcrawler.model;

import com.tarotcrawler.TestRunner;

public class GearHandsTest {

    public void testReceSieNieDubluja() {
        Hero h = new Hero(HeroClass.MAG);
        Gear a = new Gear("Miecz A", Gear.Slot.HAND_L, 10, 0, 0, 0, 0, 0);
        Gear b = new Gear("Miecz B", Gear.Slot.HAND_L, 12, 0, 0, 0, 0, 0);
        TestRunner.eq(null, h.equip(a), "pierwszy do wolnej reki");
        TestRunner.eq(null, h.equip(b), "drugi do drugiej reki");
        TestRunner.ok(h.getGear().get(Gear.Slot.HAND_L) != null, "lewa zajeta");
        TestRunner.ok(h.getGear().get(Gear.Slot.HAND_R) != null, "prawa zajeta");
    }

    public void testTrzeciaBronPodmieniaSlabsza() {
        Hero h = new Hero(HeroClass.MAG);
        Gear slaby = new Gear("Slaby", Gear.Slot.HAND_L, 2, 0, 0, 0, 0, 0);
        Gear mocny = new Gear("Mocny", Gear.Slot.HAND_L, 20, 0, 0, 0, 0, 0);
        Gear sredni = new Gear("Sredni", Gear.Slot.HAND_L, 10, 0, 0, 0, 0, 0);
        h.equip(slaby);
        h.equip(mocny);
        Gear zdjety = h.equip(sredni);
        TestRunner.eq("Slaby", zdjety.name, "podmienia slabszy");
        TestRunner.eq("Sredni", h.getGear().get(Gear.Slot.HAND_L).name, "sredni w lewej");
        TestRunner.eq("Mocny", h.getGear().get(Gear.Slot.HAND_R).name, "mocny w prawej");
    }

    public void testSlotOfIUnequip() {
        Hero h = new Hero(HeroClass.MAG);
        Gear helm = new Gear("Helm", Gear.Slot.HELM, 0, 5, 0, 0, 0, 0);
        h.equip(helm);
        TestRunner.eq(Gear.Slot.HELM, h.slotOf(helm), "slot helmu");
        TestRunner.eq(helm, h.unequip(Gear.Slot.HELM), "zdjecie zwraca");
        TestRunner.eq(null, h.getGear().get(Gear.Slot.HELM), "pusto po zdjeciu");
    }

    public void testRozdzkaDajeCzar() {
        Hero h = new Hero(HeroClass.MAG);
        TestRunner.ok(!h.getSpells().contains("PIORUN"), "na start brak pioruna");
        Gear wand = new Gear("Rozdzka", Gear.Slot.HAND_L, "PIORUN", 3, 12);
        h.equip(wand);
        TestRunner.ok(h.getSpells().contains("PIORUN"), "rozdzka daje czar");
        TestRunner.ok(h.getSpells().contains("KULA_OGNIA"), "sygnatura zostaje");
        h.unequip(h.slotOf(wand));
        TestRunner.ok(!h.getSpells().contains("PIORUN"), "po zdjeciu czar znika");
    }
}
