package com.tarotcrawler.util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Grafiki: arkusze sprite'ow (gra sama tnie klatki), pojedyncze PNG z assets/
 *  albo placeholder. Baza: ./assets, albo -Dtarot.assets=sciezka.
 *  Układ arkuszy opisuje assets/GRAFIKI_BRIEF.md */
public final class Assets {

    private static Path base;
    private static final Map<String, ImageIcon> CACHE = new HashMap<>();
    private static final Map<String, BufferedImage> SHEET_CACHE = new HashMap<>();
    private static final Set<String> LOGGED_MISSING = new HashSet<>();

    private static class SheetPos {
        final String sheet;
        final int cols;
        final int rows;
        final int col;
        final int row;

        SheetPos(String sheet, int cols, int rows, int col, int row) {
            this.sheet = sheet;
            this.cols = cols;
            this.rows = rows;
            this.col = col;
            this.row = row;
        }
    }

    private static final Map<String, SheetPos> SHEETS = new HashMap<>();

    static {
        String[] heroesA = {"glupiec", "mag", "cesarz", "kaplanka"};
        for (int i = 0; i < heroesA.length; i++) {
            SHEETS.put("hero/" + heroesA[i], new SheetPos("all/1-4 heros", 3, 4, 0, i));
        }
        String[] heroesB = {"papiez", "kochankowie", "rydwan", "sprawiedliwosc", "pustelnik"};
        for (int i = 0; i < heroesB.length; i++) {
            SHEETS.put("hero/" + heroesB[i], new SheetPos("heroes/sheet-b", 3, 5, 0, i));
        }
        // Foe entries: 1x1 sheets pointing to individual PNGs
        String[] pipSuits = {"kielichy", "miecze", "monety", "bulawy"};
        for (int i = 0; i < pipSuits.length; i++) {
            SHEETS.put("foe/pip-" + pipSuits[i], new SheetPos("foes/pip-" + pipSuits[i], 1, 1, 0, 0));
        }
        String[] courtNames = {"jopek", "rycerz", "krolowa", "krol", "as"};
        for (int i = 0; i < courtNames.length; i++) {
            SHEETS.put("foe/court-" + courtNames[i], new SheetPos("foes/court-" + courtNames[i], 1, 1, 0, 0));
        }
        String[] bosses = {"papiez", "kochankowie", "rydwan", "sprawiedliwosc", "pustelnik"};
        for (int i = 0; i < bosses.length; i++) {
            SHEETS.put("boss/" + bosses[i], new SheetPos("bosses/sheet", 3, 5, 0, i));
        }
    }

    private Assets() {}

    private static Path base() {
        if (base != null) {
            return base;
        }
        List<Path> candidates = new ArrayList<>();
        String override = System.getProperty("tarot.assets");
        if (override != null && !override.isBlank()) {
            candidates.add(Paths.get(override));
        }
        candidates.add(Paths.get(System.getProperty("user.dir"), "assets"));
        candidates.add(Paths.get("C:\\JavaProjects\\TarotCrawlerRPG\\assets"));
        for (Path p : candidates) {
            if (Files.isDirectory(p)) {
                base = p;
                break;
            }
        }
        if (base == null) {
            base = candidates.get(candidates.size() - 1);
        }
        GameLog.info("assets: baza " + base.toAbsolutePath());
        return base;
    }

    private static class Res {
        final ImageIcon icon;
        final boolean real;

        Res(ImageIcon icon, boolean real) {
            this.icon = icon;
            this.real = real;
        }
    }

    private static Res resolve(String key, int size) {
        String ck = key + "@" + size;
        ImageIcon hit = CACHE.get(ck);
        if (hit != null) {
            return new Res(hit, !LOGGED_MISSING.contains(key));
        }
        ImageIcon ic = null;
        SheetPos sp = SHEETS.get(key);
        if (sp != null) {
            ic = sheetCell(sp, size);
        }
        if (ic == null) {
            ic = loadFile(key, size);
        }
        boolean real = ic != null;
        if (!real) {
            ic = placeholder(key, size);
            if (LOGGED_MISSING.add(key)) {
                GameLog.info("assets brak: " + key + " - placeholder");
            }
        }
        CACHE.put(ck, ic);
        return new Res(ic, real);
    }

    /** Ikona o boku size px (arkusz, plik albo placeholder). */
    public static ImageIcon icon(String key, int size) {
        return resolve(key, size).icon;
    }

    /** Czy istnieje prawdziwa grafika (arkusz lub plik). */
    public static boolean exists(String key) {
        SheetPos sp = SHEETS.get(key);
        if (sp != null) {
            return sheetImage(sp.sheet) != null;
        }
        try {
            return Files.exists(base().resolve(key + ".png"));
        } catch (Exception e) {
            return false;
        }
    }

    private static final String[] EXTS = {".png", ".jpg", ".jpeg"};

    private static Path findFile(String key) {
        for (String ext : EXTS) {
            try {
                Path f = base().resolve(key + ext);
                if (Files.exists(f)) {
                    return f;
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private static BufferedImage sheetImage(String sheet) {
        if (SHEET_CACHE.containsKey(sheet)) {
            return SHEET_CACHE.get(sheet);
        }
        BufferedImage img = null;
        try {
            for (String ext : EXTS) {
                Path f = base().resolve(sheet + ext);
                if (Files.exists(f)) {
                    img = ImageIO.read(f.toFile());
                    break;
                }
            }
        } catch (Exception e) {
            GameLog.warn("assets blad arkusza " + sheet + ": " + e.getMessage());
        }
        SHEET_CACHE.put(sheet, img);
        return img;
    }

    private static boolean hasTransparentPixels(BufferedImage img) {
        int w = Math.min(img.getWidth(), 4);
        int h = Math.min(img.getHeight(), 4);
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int a = (img.getRGB(x, y) >> 24) & 255;
                if (a < 200) return true;
            }
        }
        return false;
    }

    private static ImageIcon sheetCell(SheetPos sp, int size) {
        BufferedImage sheet = sheetImage(sp.sheet);
        if (sheet == null) {
            return null;
        }
        int x0 = (sp.col * sheet.getWidth()) / sp.cols;
        int x1 = ((sp.col + 1) * sheet.getWidth()) / sp.cols;
        int y0 = (sp.row * sheet.getHeight()) / sp.rows;
        int y1 = ((sp.row + 1) * sheet.getHeight()) / sp.rows;
        if (x1 <= x0 || y1 <= y0) {
            GameLog.warn("assets zla klatka: " + sp.sheet + " (" + sp.col + "," + sp.row + ")");
            return null;
        }
        BufferedImage cell = sheet.getSubimage(x0, y0, x1 - x0, y1 - y0);
        boolean transparent = hasTransparentPixels(cell);
        Image img;
        if (transparent) {
            img = cell.getScaledInstance(size, size, Image.SCALE_SMOOTH);
        } else {
            img = toTransparent(cell).getScaledInstance(size, size, Image.SCALE_SMOOTH);
        }
        return new ImageIcon(img);
    }

    private static ImageIcon loadFile(String key, int size) {
        try {
            Path f = findFile(key);
            if (f == null) {
                return null;
            }
            BufferedImage src = ImageIO.read(f.toFile());
            if (src == null) {
                return null;
            }
            boolean hasAlpha = src.getColorModel().hasAlpha();
            Image img = (hasAlpha ? src : toTransparent(src))
                    .getScaledInstance(size, size, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            GameLog.warn("assets blad przy " + key + ": " + e.getMessage());
            return null;
        }
    }

    /** Wycina tlo: flood fill od krawedzi + globalnie jasne tla.
     *  Progowanie: srednia >= 225 (flood) lub >= 240 (global) zeby nie zjadac postaci. */
    static BufferedImage toTransparent(BufferedImage src) {
        int w = src.getWidth();
        int h = src.getHeight();
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.drawImage(src, 0, 0, null);
        g.dispose();

        boolean bgIsLight = isBackgroundLight(img);

        boolean[][] seen = new boolean[w][h];
        java.util.ArrayDeque<int[]> stack = new java.util.ArrayDeque<>();
        for (int x = 0; x < w; x++) {
            stack.push(new int[]{x, 0});
            stack.push(new int[]{x, h - 1});
        }
        for (int y = 0; y < h; y++) {
            stack.push(new int[]{0, y});
            stack.push(new int[]{w - 1, y});
        }
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        while (!stack.isEmpty()) {
            int[] p = stack.pop();
            int x = p[0], y = p[1];
            if (x < 0 || y < 0 || x >= w || y >= h || seen[x][y]) continue;
            seen[x][y] = true;
            int rgb = img.getRGB(x, y);
            int a = (rgb >> 24) & 255;
            if (a < 10) continue;
            int r = (rgb >> 16) & 255;
            int gg = (rgb >> 8) & 255;
            int b = rgb & 255;
            double avg = (r + gg + b) / 3.0;
            if (bgIsLight) {
                if (avg < 200) continue;
            } else {
                if (r > 50 || gg > 50 || b > 50) continue;
            }
            img.setRGB(x, y, 0);
            for (int[] d : dirs) {
                stack.push(new int[]{x + d[0], y + d[1]});
            }
        }
        return img;
    }

    private static boolean isBackgroundLight(BufferedImage img) {
        int w = img.getWidth(), h = img.getHeight();
        int[] samples = {img.getRGB(0, 0), img.getRGB(w - 1, 0),
                img.getRGB(0, h - 1), img.getRGB(w - 1, h - 1),
                img.getRGB(w / 2, 0), img.getRGB(0, h / 2)};
        long total = 0;
        for (int s : samples) {
            int r = (s >> 16) & 255, gg = (s >> 8) & 255, b = s & 255;
            total += r + gg + b;
        }
        return total / samples.length / 3.0 > 128;
    }

    /** Placeholder: ciemny kafelek, zlota ramka, duzy kod (symbol/liczba). */
    public static ImageIcon placeholder(String key, int size) {
        BufferedImage img = new BufferedImage(Math.max(1, size), Math.max(1, size),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        try {
            g.setColor(new Color(25, 25, 35));
            g.fillRect(0, 0, size, size);
            g.setColor(new Color(218, 165, 32));
            g.drawRect(1, 1, size - 3, size - 3);
            String code = codeOf(key);
            g.setColor(Color.LIGHT_GRAY);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.max(10, size / 3)));
            FontMetrics fm = g.getFontMetrics();
            int w = fm.stringWidth(code);
            g.drawString(code, (size - w) / 2, size / 2 + fm.getAscent() / 2);
        } finally {
            g.dispose();
        }
        return new ImageIcon(img);
    }

    private static String codeOf(String key) {
        int slash = key.lastIndexOf('/');
        String tail = slash >= 0 ? key.substring(slash + 1) : key;
        if (tail.length() <= 3) {
            return tail.toUpperCase();
        }
        return tail.substring(0, 2).toUpperCase();
    }

    /** Portret na labelce: prawdziwa grafika albo barwiony placeholder z symbolem. */
    public static void portrait(JLabel label, String key, String code, int size, Color bg) {
        Res r = resolve(key, size);
        label.setIcon(r.real ? r.icon : tintedPlaceholder(code, size, bg));
        label.setText("");
    }

    private static ImageIcon tintedPlaceholder(String code, int size, Color bg) {
        BufferedImage img = new BufferedImage(Math.max(1, size), Math.max(1, size),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        try {
            g.setColor(bg);
            g.fillRect(0, 0, size, size);
            g.setColor(new Color(218, 165, 32));
            g.drawRect(1, 1, size - 3, size - 3);
            g.setColor(Color.WHITE);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.max(12, size / 2)));
            FontMetrics fm = g.getFontMetrics();
            int w = fm.stringWidth(code);
            g.drawString(code, (size - w) / 2, size / 2 + fm.getAscent() / 2);
        } finally {
            g.dispose();
        }
        return new ImageIcon(img);
    }
}
