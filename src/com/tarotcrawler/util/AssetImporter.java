package com.tarotcrawler.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Importuje pobrane darmowe assety i przetwarza je na format gry.
 * ZrodlO: OpenGameArt.org (CC0 / CC-BY) + Tarot de Marseille (public domain)
 * Uruchom: java -cp out com.tarotcrawler.util.AssetImporter
 */
public final class AssetImporter {

    private static final String DL = "C:\\JavaProjects\\TarotCrawlerRPG\\assets\\_download";
    private static final String OUT = "C:\\JavaProjects\\TarotCrawlerRPG\\assets";
    private static final int CELL = 128;
    private static final int BG_W = 800, BG_H = 600;

    public static void main(String[] args) throws Exception {
        new File(OUT + "\\bg").mkdirs();
        new File(OUT + "\\heroes").mkdirs();
        new File(OUT + "\\bosses").mkdirs();
        new File(OUT + "\\foes").mkdirs();
        new File(OUT + "\\icons").mkdirs();
        new File(OUT + "\\cards").mkdirs();
        new File(OUT + "\\all").mkdirs();

        importDarkFantasyBackgrounds();
        importTarotBossCards();

        System.out.println("AssetImporter: gotowe!");
    }

    // ==================== DARK FANTASY BACKGROUNDS ====================

    private static void importDarkFantasyBackgrounds() throws Exception {
        Map<String, String> bgMap = new HashMap<>();
        bgMap.put("kielichy", "catacombs");
        bgMap.put("miecze", "mausoleum");
        bgMap.put("monety", "ossuary");
        bgMap.put("bulawy", "blood_moon");
        bgMap.put("lobby", "dungeon_cell");

        for (Map.Entry<String, String> e : bgMap.entrySet()) {
            String suit = e.getKey();
            String src = e.getValue();
            File srcFile = new File(DL + "\\" + src + ".png");
            if (!srcFile.exists()) {
                System.out.println("AssetImporter: brak " + src + ".png");
                continue;
            }
            BufferedImage img = ImageIO.read(srcFile);
            BufferedImage bg = new BufferedImage(BG_W, BG_H, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = bg.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(img, 0, 0, BG_W, BG_H, null);
            g.dispose();
            ImageIO.write(bg, "png", new File(OUT + "\\bg\\" + suit + ".png"));
            System.out.println("AssetImporter: bg/" + suit + ".png (dark fantasy)");
        }
    }

    // ==================== TAROT BOSS CARDS ====================

    private static void importTarotBossCards() throws Exception {
        File tarotDir = new File(DL + "\\tarot\\tarot_de_marseilles_major_arcana");
        if (!tarotDir.exists()) {
            System.out.println("AssetImporter: brak tarot de marseille");
            return;
        }

        Map<Integer, String> bossMap = new HashMap<>();
        bossMap.put(5, "papiez");
        bossMap.put(6, "kochankowie");
        bossMap.put(7, "rydwan");
        bossMap.put(8, "sprawiedliwosc");
        bossMap.put(9, "pustelnik");

        for (Map.Entry<Integer, String> e : bossMap.entrySet()) {
            int num = e.getKey();
            String name = e.getValue();
            String filename = String.format("%02d_", num);
            File[] matches = tarotDir.listFiles((dir, n) -> n.startsWith(filename) && n.endsWith(".png"));
            if (matches == null || matches.length == 0) continue;

            BufferedImage card = ImageIO.read(matches[0]);
            int cardW = 256, cardH = 480;
            BufferedImage scaled = new BufferedImage(cardW, cardH, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = scaled.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(card, 0, 0, cardW, cardH, null);
            g.dispose();

            ImageIO.write(scaled, "png", new File(OUT + "\\cards\\boss_" + name + ".png"));
            System.out.println("AssetImporter: cards/boss_" + name + ".png");
        }

        // The Fool card (menu)
        File[] foolFiles = tarotDir.listFiles((dir, n) -> n.startsWith("00_") && n.endsWith(".png"));
        if (foolFiles != null && foolFiles.length > 0) {
            BufferedImage fool = ImageIO.read(foolFiles[0]);
            BufferedImage scaled = new BufferedImage(256, 480, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = scaled.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(fool, 0, 0, 256, 480, null);
            g.dispose();
            ImageIO.write(scaled, "png", new File(OUT + "\\cards\\fool.png"));
            System.out.println("AssetImporter: cards/fool.png");
        }

        // Menu: blood_moon z lekkim przyciemnieniem + tytul
        File menuSrc = new File(DL + "\\blood_moon.png");
        if (menuSrc.exists()) {
            BufferedImage moon = ImageIO.read(menuSrc);
            BufferedImage bg = new BufferedImage(BG_W, BG_H, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = bg.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(moon, 0, 0, BG_W, BG_H, null);
            g.setColor(new Color(0, 0, 0, 120));
            g.fillRect(0, 0, BG_W, BG_H);
            g.dispose();
            ImageIO.write(bg, "png", new File(OUT + "\\bg\\menu.png"));
            System.out.println("AssetImporter: bg/menu.png (dark fantasy)");
        }
    }
}
