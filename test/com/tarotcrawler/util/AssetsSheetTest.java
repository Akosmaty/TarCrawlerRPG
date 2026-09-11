package com.tarotcrawler.util;

import com.tarotcrawler.TestRunner;
import javax.swing.ImageIcon;

public class AssetsSheetTest {

    private static final String BASE = "C:\\JavaProjects\\TarotCrawlerRPG\\assets";

    public void testArkusz14() {
        System.setProperty("tarot.assets", BASE);
        TestRunner.ok(Assets.exists("hero/glupiec"), "glupiec w arkuszu");
        TestRunner.ok(Assets.exists("hero/mag"), "mag w arkuszu");
        TestRunner.ok(Assets.exists("hero/cesarz"), "cesarz w arkuszu");
        TestRunner.ok(Assets.exists("hero/kaplanka"), "kaplanka w arkuszu");
        ImageIcon ic = Assets.icon("hero/mag", 64);
        TestRunner.eq(64, ic.getIconWidth(), "szerokosc klatki");
        TestRunner.eq(64, ic.getIconHeight(), "wysokosc klatki");
    }

    public void testBrakWPustce() {
        System.setProperty("tarot.assets", BASE);
        TestRunner.ok(!Assets.exists("hero/nieistnieje"), "brak klucza");
        TestRunner.ok(Assets.exists("boss/papiez"), "boss jest z arkusza");
        TestRunner.ok(Assets.exists("foe/pip-kielichy"), "pip z arkusza foes");
    }

    public void testBialyTloWyciete() {
        System.setProperty("tarot.assets", BASE);
        javax.swing.ImageIcon ic = Assets.icon("hero/mag", 64);
        TestRunner.ok(ic != null && ic.getIconWidth() == 64, "ikona maga laduje sie poprawnie");
        TestRunner.ok(Assets.exists("foe/pip-miecze"), "pip-miecze istnieje");
        TestRunner.ok(Assets.exists("foe/court-jopek"), "court-jopek istnieje");
        TestRunner.ok(Assets.exists("boss/papiez"), "boss papiez istnieje");
    }
}
