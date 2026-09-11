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
        java.awt.image.BufferedImage bi =
                new java.awt.image.BufferedImage(64, 64, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        bi.getGraphics().drawImage(ic.getImage(), 0, 0, null);
        int clear = 0;
        for (int x = 0; x < 64; x += 2) {
            for (int y = 0; y < 64; y += 2) {
                if (((bi.getRGB(x, y) >>> 24) & 255) == 0) {
                    clear++;
                }
            }
        }
        TestRunner.ok(clear > 100, "biel tla wycieta, przezroczystych=" + clear);
    }
}
