package com.tarotcrawler.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

public final class AssetGenerator {
    private static final int CELL = 128;
    private static final String[] SUITS = {"kielichy", "miecze", "monety", "bulawy"};
    private static final Color[] SUIT_COLORS = {
        new Color(50, 100, 200),  // kielichy - blue
        new Color(180, 180, 200), // miecze - silver
        new Color(210, 170, 50),  // monety - gold
        new Color(200, 60, 40)   // bulawy - red
    };
    private static final String[] COURTS = {"jopek", "rycerz", "krolowa", "krol", "as"};
    private static final String[] HEROES_B = {"papiez", "kochankowie", "rydwan", "sprawiedliwosc", "pustelnik"};
    private static final Color[] HERO_COLORS = {
        new Color(180, 140, 60),  // papiez - gold/white
        new Color(200, 80, 120),  // kochankowie - pink/red
        new Color(100, 160, 200), // rydwan - blue/silver
        new Color(60, 180, 120),  // sprawiedliwosc - green
        new Color(120, 100, 140)  // pustelnik - purple
    };
    private static final Random RNG = new Random(42);

    public static void main(String[] args) throws Exception {
        String base = "C:\\JavaProjects\\TarotCrawlerRPG\\assets";
        new File(base + "\\heroes").mkdirs();
        new File(base + "\\foes").mkdirs();
        new File(base + "\\bosses").mkdirs();
        new File(base + "\\bg").mkdirs();
        new File(base + "\\icons").mkdirs();

        generateHeroesB(base);
        generatePips(base);
        generateCourts(base);
        generateBosses(base);
        generateBackgrounds(base);
        generateIcons(base);
        GameLog.info("AssetGenerator: wygenerowano wszystkie grafiki");
    }

    static void generateHeroesB(String base) throws Exception {
        BufferedImage sheet = new BufferedImage(3 * CELL, 5 * CELL, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = sheet.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (int row = 0; row < 5; row++) {
            Color c = HERO_COLORS[row];
            for (int col = 0; col < 3; col++) {
                int x = col * CELL, y = row * CELL;
                drawCharacter(g, x, y, c, row, col, HEROES_B[row]);
            }
        }
        g.dispose();
        ImageIO.write(sheet, "png", new File(base + "\\heroes\\sheet-b.png"));
        GameLog.info("AssetGenerator: heroes/sheet-b.png");
    }

    static void generatePips(String base) throws Exception {
        BufferedImage sheet = new BufferedImage(4 * CELL, CELL, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = sheet.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (int col = 0; col < 4; col++) {
            int x = col * CELL;
            drawPip(g, x, 0, SUIT_COLORS[col], col);
        }
        g.dispose();
        ImageIO.write(sheet, "png", new File(base + "\\foes\\pips.png"));
        GameLog.info("AssetGenerator: foes/pips.png");
    }

    static void generateCourts(String base) throws Exception {
        BufferedImage sheet = new BufferedImage(5 * CELL, CELL, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = sheet.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (int col = 0; col < 5; col++) {
            int x = col * CELL;
            Color c = SUIT_COLORS[col % 4];
            drawCourt(g, x, 0, c, col);
        }
        g.dispose();
        ImageIO.write(sheet, "png", new File(base + "\\foes\\courts.png"));
        GameLog.info("AssetGenerator: foes/courts.png");
    }

    static void generateBosses(String base) throws Exception {
        BufferedImage sheet = new BufferedImage(3 * CELL, 5 * CELL, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = sheet.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (int row = 0; row < 5; row++) {
            Color c = HERO_COLORS[row];
            for (int col = 0; col < 3; col++) {
                int x = col * CELL, y = row * CELL;
                drawBoss(g, x, y, c, row, col);
            }
        }
        g.dispose();
        ImageIO.write(sheet, "png", new File(base + "\\bosses\\sheet.png"));
        GameLog.info("AssetGenerator: bosses/sheet.png");
    }

    static void generateBackgrounds(String base) throws Exception {
        Color[][] palettes = {
            {new Color(20, 30, 60), new Color(40, 60, 120), new Color(80, 120, 180)},   // kielichy - blue
            {new Color(40, 40, 50), new Color(80, 80, 90), new Color(140, 140, 160)},   // miecze - grey
            {new Color(50, 40, 20), new Color(120, 100, 40), new Color(200, 170, 60)},  // monety - gold
            {new Color(60, 20, 15), new Color(160, 50, 30), new Color(220, 100, 50)}    // bulawy - red
        };
        for (int i = 0; i < 4; i++) {
            BufferedImage bg = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = bg.createGraphics();
            drawBackground(g, 800, 600, palettes[i]);
            g.dispose();
            ImageIO.write(bg, "png", new File(base + "\\bg\\" + SUITS[i] + ".png"));
            GameLog.info("AssetGenerator: bg/" + SUITS[i] + ".png");
        }
    }

    static void generateIcons(String base) throws Exception {
        String[] names = {"helm", "armor", "boots", "sword", "shield", "amulet", "wand", "key"};
        Color[] colors = {
            new Color(180, 180, 180), new Color(100, 100, 120), new Color(140, 100, 60),
            new Color(200, 200, 210), new Color(160, 140, 60), new Color(200, 80, 160),
            new Color(100, 60, 180), new Color(210, 180, 50)
        };
        for (int i = 0; i < names.length; i++) {
            BufferedImage icon = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = icon.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            drawIcon(g, names[i], colors[i]);
            g.dispose();
            ImageIO.write(icon, "png", new File(base + "\\icons\\" + names[i] + ".png"));
        }
        GameLog.info("AssetGenerator: icons/*");
    }

    private static void drawCharacter(Graphics2D g, int ox, int oy, Color c, int row, int col, String name) {
        // Dark background tile
        g.setColor(new Color(15, 12, 20));
        g.fillRect(ox, oy, CELL, CELL);
        // Ground shadow
        g.setColor(new Color(0, 0, 0, 80));
        g.fillOval(ox + 24, oy + 96, 80, 20);
        // Body - humanoid shape
        Color body = c;
        Color dark = c.darker().darker();
        // Legs
        g.setColor(dark);
        g.fillRect(ox + 44, oy + 80, 16, 24);
        g.fillRect(ox + 68, oy + 80, 16, 24);
        // Torso
        g.setColor(body);
        g.fillRoundRect(ox + 32, oy + 40, 64, 44, 12, 12);
        // Head
        g.setColor(body.brighter());
        g.fillOval(ox + 40, oy + 12, 48, 40);
        // Eyes
        g.setColor(new Color(220, 200, 160));
        g.fillOval(ox + 48, oy + 24, 8, 8);
        g.fillOval(ox + 68, oy + 24, 8, 8);
        g.setColor(Color.BLACK);
        g.fillOval(ox + 50, oy + 26, 4, 4);
        g.fillOval(ox + 70, oy + 26, 4, 4);
        // Row-specific features
        if (col == 0) { // portrait/idle - add class symbol
            g.setColor(new Color(210, 180, 50, 180));
            drawTarotSymbol(g, ox + 64, oy + 58, row);
        } else if (col == 1) { // attack - weapon raised
            g.setColor(new Color(200, 200, 210));
            g.fillRect(ox + 90, oy + 20, 6, 50);
            g.setColor(new Color(210, 180, 50));
            g.fillRect(ox + 84, oy + 16, 18, 10);
        } else { // hurt - red flash tint
            g.setColor(new Color(255, 0, 0, 60));
            g.fillRect(ox, oy, CELL, CELL);
        }
    }

    private static void drawTarotSymbol(Graphics2D g, int cx, int cy, int type) {
        switch (type) {
            case 0: // Papiez - cross/staff
                g.fillRect(cx - 2, cy - 14, 4, 28);
                g.fillRect(cx - 8, cy - 8, 16, 4);
                break;
            case 1: // Kochankowie - two circles
                g.drawOval(cx - 12, cy - 8, 12, 12);
                g.drawOval(cx + 0, cy - 8, 12, 12);
                break;
            case 2: // Rydwan - triangle
                int[] tx = {cx, cx - 10, cx + 10};
                int[] ty = {cy - 12, cy + 8, cy + 8};
                g.fillPolygon(tx, ty, 3);
                break;
            case 3: // Sprawiedliwosc - scales
                g.fillRect(cx - 1, cy - 14, 2, 20);
                g.fillRect(cx - 12, cy - 10, 24, 2);
                g.fillOval(cx - 14, cy - 6, 8, 8);
                g.fillOval(cx + 6, cy - 6, 8, 8);
                break;
            case 4: // Pustelnik - lantern
                g.setColor(new Color(210, 180, 50));
                g.fillRect(cx - 4, cy - 10, 8, 14);
                g.setColor(new Color(255, 220, 80, 200));
                g.fillOval(cx - 3, cy - 8, 6, 8);
                break;
        }
    }

    private static void drawPip(Graphics2D g, int ox, int oy, Color c, int suitIdx) {
        // Dark background
        g.setColor(new Color(15, 12, 20));
        g.fillRect(ox, oy, CELL, CELL);
        // Ethereal spirit shape
        Color glow = new Color(c.getRed(), c.getGreen(), c.getBlue(), 80);
        g.setColor(glow);
        g.fillOval(ox + 16, oy + 16, 96, 96);
        // Core
        g.setColor(c);
        g.fillOval(ox + 32, oy + 32, 64, 64);
        // Eyes
        g.setColor(new Color(255, 255, 200));
        g.fillOval(ox + 44, oy + 50, 12, 12);
        g.fillOval(ox + 68, oy + 50, 12, 12);
        g.setColor(new Color(20, 10, 30));
        g.fillOval(ox + 47, oy + 53, 6, 6);
        g.fillOval(ox + 71, oy + 53, 6, 6);
        // Suit element wisps
        g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 140));
        for (int i = 0; i < 3; i++) {
            int wx = ox + 20 + RNG.nextInt(88);
            int wy = oy + 80 + RNG.nextInt(30);
            g.fillOval(wx, wy, 8 + RNG.nextInt(8), 6 + RNG.nextInt(6));
        }
        // Mouth
        g.setColor(c.darker());
        g.drawArc(ox + 48, oy + 64, 32, 16, 0, -180);
    }

    private static void drawCourt(Graphics2D g, int ox, int oy, Color c, int courtIdx) {
        // Dark background
        g.setColor(new Color(15, 12, 20));
        g.fillRect(ox, oy, CELL, CELL);
        // Ground shadow
        g.setColor(new Color(0, 0, 0, 80));
        g.fillOval(ox + 20, oy + 96, 88, 20);
        Color dark = c.darker();
        // Legs
        g.setColor(dark);
        g.fillRect(ox + 40, oy + 80, 18, 24);
        g.fillRect(ox + 68, oy + 80, 18, 24);
        // Body
        g.setColor(c);
        g.fillRoundRect(ox + 28, oy + 38, 72, 46, 14, 14);
        // Crown/head
        g.setColor(c.brighter());
        g.fillOval(ox + 38, oy + 10, 52, 38);
        // Court-specific crown
        g.setColor(new Color(210, 180, 50));
        switch (courtIdx) {
            case 0: // Jopek - small cap
                g.fillArc(ox + 42, oy + 4, 44, 20, 0, 180);
                break;
            case 1: // Rycerz - helmet
                g.fillRect(ox + 36, oy + 4, 56, 12);
                g.fillRect(ox + 58, oy + 0, 12, 20);
                break;
            case 2: // Krolowa - tiara
                int[] tx = {ox + 44, ox + 52, ox + 60, ox + 68, ox + 76};
                int[] ty = {oy + 12, oy + 2, oy + 10, oy + 2, oy + 12};
                g.fillPolygon(tx, ty, 5);
                break;
            case 3: // Krol - big crown
                g.fillRect(ox + 36, oy + 2, 56, 14);
                for (int i = 0; i < 4; i++) {
                    g.fillRect(ox + 40 + i * 14, oy - 4, 8, 10);
                }
                break;
            case 4: // As - halo/essence
                g.setColor(new Color(210, 180, 50, 100));
                g.drawOval(ox + 24, oy + 4, 80, 80);
                g.setColor(new Color(210, 180, 50));
                g.drawOval(ox + 30, oy + 10, 68, 68);
                break;
        }
        // Eyes
        g.setColor(new Color(220, 200, 160));
        g.fillOval(ox + 44, oy + 22, 10, 10);
        g.fillOval(ox + 68, oy + 22, 10, 10);
        g.setColor(Color.BLACK);
        g.fillOval(ox + 47, oy + 25, 5, 5);
        g.fillOval(ox + 71, oy + 25, 5, 5);
        // Court emblem on chest
        g.setColor(new Color(210, 180, 50, 200));
        g.fillOval(ox + 52, oy + 50, 24, 20);
    }

    private static void drawBoss(Graphics2D g, int ox, int oy, Color c, int row, int col) {
        if (col == 1) {
            // Attack frame - boss + attack effect
            drawBossBody(g, ox, oy, c, row);
            g.setColor(new Color(255, 100, 50, 120));
            g.fillOval(ox - 20, oy + 20, CELL + 40, CELL - 20);
        } else if (col == 2) {
            // Special ability frame
            drawBossBody(g, ox, oy, c, row);
            g.setColor(new Color(200, 180, 255, 100));
            g.fillOval(ox + 8, oy + 8, CELL - 16, CELL - 16);
            g.setColor(new Color(255, 255, 200, 80));
            g.fillOval(ox + 20, oy + 20, CELL - 40, CELL - 40);
        } else {
            // Portrait frame - darker, more menacing
            drawBossBody(g, ox, oy, c.darker(), row);
            // Menacing aura
            g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 40));
            g.fillOval(ox - 10, oy - 10, CELL + 20, CELL + 20);
        }
    }

    private static void drawBossBody(Graphics2D g, int ox, int oy, Color c, int row) {
        // Dark background
        g.setColor(new Color(10, 8, 15));
        g.fillRect(ox, oy, CELL, CELL);
        // Ground
        g.setColor(new Color(0, 0, 0, 100));
        g.fillOval(ox + 12, oy + 92, 104, 24);
        Color dark = c.darker().darker();
        // Legs - thicker than regular
        g.setColor(dark);
        g.fillRect(ox + 36, oy + 76, 22, 28);
        g.fillRect(ox + 70, oy + 76, 22, 28);
        // Body - bulkier
        g.setColor(c);
        g.fillRoundRect(ox + 22, oy + 32, 84, 48, 16, 16);
        // Head
        g.setColor(c.brighter());
        g.fillOval(ox + 34, oy + 6, 60, 40);
        // Horns
        g.setColor(dark);
        int[] hx1 = {ox + 34, ox + 24, ox + 30};
        int[] hy1 = {oy + 10, oy - 6, oy + 6};
        g.fillPolygon(hx1, hy1, 3);
        int[] hx2 = {ox + 94, ox + 104, ox + 98};
        int[] hy2 = {oy + 10, oy - 6, oy + 6};
        g.fillPolygon(hx2, hy2, 3);
        // Eyes - glowing
        g.setColor(new Color(255, 200, 60));
        g.fillOval(ox + 42, oy + 18, 14, 14);
        g.fillOval(ox + 68, oy + 18, 14, 14);
        g.setColor(new Color(200, 40, 20));
        g.fillOval(ox + 46, oy + 22, 8, 8);
        g.fillOval(ox + 72, oy + 22, 8, 8);
        // Boss emblem
        g.setColor(new Color(210, 180, 50, 200));
        drawTarotSymbol(g, ox + 64, oy + 52, row);
    }

    private static void drawBackground(Graphics2D g, int w, int h, Color[] palette) {
        // Gradient base
        for (int y = 0; y < h; y++) {
            float t = (float) y / h;
            int r = lerp(palette[0].getRed(), palette[1].getRed(), t);
            int gr = lerp(palette[0].getGreen(), palette[1].getGreen(), t);
            int b = lerp(palette[0].getBlue(), palette[1].getBlue(), t);
            g.setColor(new Color(r, gr, b));
            g.fillRect(0, y, w, 1);
        }
        // Stone texture overlay
        for (int i = 0; i < 200; i++) {
            int bx = RNG.nextInt(w), by = RNG.nextInt(h);
            int bw = 20 + RNG.nextInt(60), bh = 15 + RNG.nextInt(40);
            float alpha = 0.05f + RNG.nextFloat() * 0.1f;
            g.setColor(new Color(palette[2].getRed(), palette[2].getGreen(), palette[2].getBlue(),
                    (int)(alpha * 255)));
            g.fillRect(bx, by, bw, bh);
            g.setColor(new Color(0, 0, 0, (int)(alpha * 128)));
            g.drawRect(bx, by, bw, bh);
        }
        // Vignette corners
        for (int i = 0; i < 60; i++) {
            int alpha = (int)(255 * (1.0 - i / 60.0) * 0.3);
            g.setColor(new Color(0, 0, 0, alpha));
            g.fillRect(0, 0, w, i * 3);
            g.fillRect(0, h - i * 3, w, i * 3);
            g.fillRect(0, 0, i * 4, h);
            g.fillRect(w - i * 4, 0, i * 4, h);
        }
    }

    private static void drawIcon(Graphics2D g, String name, Color c) {
        g.setColor(new Color(0, 0, 0, 0)); // transparent
        g.clearRect(0, 0, 32, 32);
        g.setColor(c);
        switch (name) {
            case "helm":
                g.fillArc(4, 8, 24, 18, 0, 180);
                g.fillRect(6, 16, 20, 8);
                g.fillRect(14, 4, 4, 8);
                break;
            case "armor":
                g.fillRoundRect(6, 6, 20, 22, 6, 6);
                g.setColor(c.darker());
                g.fillRect(10, 14, 12, 2);
                break;
            case "boots":
                g.fillRect(6, 12, 8, 16);
                g.fillRect(18, 12, 8, 16);
                g.fillRect(4, 24, 12, 6);
                g.fillRect(16, 24, 12, 6);
                break;
            case "sword":
                g.fillRect(14, 2, 4, 20);
                g.setColor(new Color(210, 180, 50));
                g.fillRect(8, 20, 16, 4);
                g.fillRect(12, 24, 8, 6);
                break;
            case "shield":
                g.fillOval(4, 4, 24, 24);
                g.setColor(new Color(210, 180, 50));
                g.drawOval(8, 8, 16, 16);
                g.fillRect(14, 8, 4, 16);
                g.fillRect(8, 14, 16, 4);
                break;
            case "amulet":
                g.setColor(new Color(210, 180, 50));
                g.drawOval(8, 4, 16, 16);
                g.fillOval(12, 8, 8, 8);
                g.fillRect(14, 20, 4, 10);
                break;
            case "wand":
                g.fillRect(14, 4, 4, 24);
                g.setColor(new Color(200, 100, 255));
                g.fillOval(10, 0, 12, 12);
                g.setColor(new Color(255, 255, 200));
                g.fillOval(12, 2, 8, 8);
                break;
            case "key":
                g.setColor(new Color(210, 180, 50));
                g.fillOval(6, 2, 14, 14);
                g.setColor(new Color(0, 0, 0, 0));
                g.clearRect(9, 5, 8, 8);
                g.setColor(c);
                g.fillRect(14, 12, 4, 18);
                g.fillRect(14, 24, 10, 4);
                g.fillRect(14, 18, 8, 4);
                break;
        }
    }

    private static int lerp(int a, int b, float t) {
        return (int)(a + (b - a) * t);
    }
}
