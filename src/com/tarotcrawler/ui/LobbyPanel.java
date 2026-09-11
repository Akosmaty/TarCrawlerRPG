package com.tarotcrawler.ui;

import com.tarotcrawler.model.Dungeon;
import com.tarotcrawler.model.HeroClass;
import com.tarotcrawler.util.Assets;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/** Chodzalne lobby-loch 10x10. Na gorze 4 szare kafle-wejscia do lochow. */
public class LobbyPanel extends JPanel {

    public static final int SIZE = 10;

    private HeroClass hero = HeroClass.GLUPIEC;
    private int playerRow = SIZE - 1;
    private int playerCol = 4;
    private final Color[][] cellAlpha = new Color[SIZE][SIZE];
    private final Map<String, Dungeon> entrances = new HashMap<>();
    private java.util.Set<Dungeon> cleared = java.util.Collections.emptySet();
    private Consumer<Dungeon> onEnter = d -> {};

    private static class LobbyGrid extends JPanel {
        private BufferedImage bgImg;
        private final Color[][] cellAlpha;
        LobbyGrid(Color[][] cellAlpha) {
            super(new GridLayout(SIZE, SIZE, 2, 2));
            setOpaque(false);
            this.cellAlpha = cellAlpha;
        }
        void setBgImg(BufferedImage img) { this.bgImg = img; repaint(); }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(new Color(20, 20, 25));
            g2.fillRect(0, 0, getWidth(), getHeight());
            if (bgImg != null) {
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.drawImage(bgImg, 0, 0, getWidth(), getHeight(), null);
            }
            super.paintComponent(g);
            for (int i = 0; i < getComponentCount(); i++) {
                int r = i / SIZE, c = i % SIZE;
                if (r < SIZE && c < SIZE && cellAlpha[r][c] != null) {
                    Component comp = getComponent(i);
                    g2.setColor(cellAlpha[r][c]);
                    g2.fillRect(comp.getX(), comp.getY(), comp.getWidth(), comp.getHeight());
                }
            }
        }
    }

    private final LobbyGrid grid = new LobbyGrid(cellAlpha);

    public LobbyPanel() {
        super(new BorderLayout());
        setBackground(Color.BLACK);

        grid.setBorder(BorderFactory.createEmptyBorder(8, 24, 8, 24));
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                JLabel l = new JLabel("", SwingConstants.CENTER);
                l.setOpaque(false);
                l.setFont(new Font("SansSerif", Font.BOLD, 18));
                l.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 70, 80)));
                grid.add(l);
            }
        }
        add(grid, BorderLayout.CENTER);

        Dungeon[] ds = Dungeon.values();
        int[] cols = {1, 3, 6, 8};
        for (int i = 0; i < ds.length && i < cols.length; i++) {
            entrances.put(0 + "," + cols[i], ds[i]);
        }

        loadBg();
        setupKeys();
        render();
    }

    private void loadBg() {
        String userDir = System.getProperty("user.dir");
        String[] exts = {".png", ".jpg", ".jpeg"};
        for (String ext : exts) {
            try {
                java.nio.file.Path p = java.nio.file.Paths.get(userDir, "assets", "bg/lobby" + ext);
                if (java.nio.file.Files.exists(p)) {
                    grid.setBgImg(javax.imageio.ImageIO.read(p.toFile()));
                    return;
                }
            } catch (Exception ignored) {}
        }
        for (String ext : exts) {
            try {
                java.nio.file.Path p = java.nio.file.Paths.get(
                        "C:\\JavaProjects\\TarotCrawlerRPG\\assets", "bg/lobby" + ext);
                if (java.nio.file.Files.exists(p)) {
                    grid.setBgImg(javax.imageio.ImageIO.read(p.toFile()));
                    return;
                }
            } catch (Exception ignored) {}
        }
    }

    private void setupKeys() {
    }

    public void setOnEnter(Consumer<Dungeon> onEnter) {
        this.onEnter = onEnter;
    }

    public void setHero(HeroClass hero) {
        this.hero = hero;
        render();
    }

    public void resetPlayer() {
        playerRow = SIZE - 1;
        playerCol = 4;
        render();
    }

    /** Ruch z centralnego dispatchera (MainFrame): tylko strzalki. */
    public void move(int dx, int dy) {
        step(dx, dy);
    }

    private void step(int dx, int dy) {
        int nr = playerRow + dy;
        int nc = playerCol + dx;
        if (nr < 0 || nr >= SIZE || nc < 0 || nc >= SIZE) {
            return;
        }
        playerRow = nr;
        playerCol = nc;
        render();
        Dungeon d = entrances.get(playerRow + "," + playerCol);
        if (d != null) {
            onEnter.accept(d);
        }
    }

    public void setCleared(java.util.Set<Dungeon> cleared) {
        this.cleared = cleared;
        render();
    }

    private JLabel cellAt(int r, int c) {
        return (JLabel) grid.getComponent(r * SIZE + c);
    }

    private void render() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                JLabel l = cellAt(r, c);
                cellAlpha[r][c] = null;
                Dungeon d = entrances.get(r + "," + c);
                if (r == playerRow && c == playerCol) {
                    l.setIcon(Assets.icon("hero/" + hero.name().toLowerCase(java.util.Locale.ROOT), 32));
                    l.setText("");
                    cellAlpha[r][c] = d != null ? new Color(128, 128, 128, 160) : new Color(40, 40, 50, 80);
                } else if (d != null) {
                    boolean done = cleared.contains(d);
                    l.setIcon(null);
                    l.setText((done ? "\u2713" : "") + d.name().substring(0, 1));
                    l.setForeground(Color.BLACK);
                    cellAlpha[r][c] = done ? new Color(60, 179, 113, 180) : new Color(160, 160, 160, 180);
                    l.setToolTipText(d.getName() + (done ? " (wyczyszczony)" : ""));
                } else {
                    l.setIcon(null);
                    l.setText("");
                    cellAlpha[r][c] = new Color(40, 40, 50, 50);
                    l.setToolTipText(null);
                }
            }
        }
        grid.repaint();
    }
}
