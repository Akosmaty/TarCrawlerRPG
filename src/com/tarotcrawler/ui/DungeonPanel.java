package com.tarotcrawler.ui;

import com.tarotcrawler.model.Dungeon;
import com.tarotcrawler.model.Enemy;
import com.tarotcrawler.model.EnemyTable;
import com.tarotcrawler.model.Gear;
import com.tarotcrawler.model.Hero;
import com.tarotcrawler.model.HeroClass;
import com.tarotcrawler.model.LootTable;
import com.tarotcrawler.model.RunState;
import com.tarotcrawler.model.Spell;
import com.tarotcrawler.util.Assets;
import com.tarotcrawler.util.GameLog;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Wnetrze lochu 10x10. Jedno okno: dolny pasek akcji zamiast popupow. */
public class DungeonPanel extends JPanel {

    public static final int SIZE = 10;
    public static final int LAST_FLOOR = 10;

    public interface Listener {
        void onDungeonEnd(RunState run, boolean cleared, boolean died);
    }

    public interface BattleStarter {
        void start(RunState run, java.util.List<Enemy> foes, boolean boss,
                   java.util.function.Consumer<BattlePanel.Result> cb);
    }

    private enum Tile { FLOOR, FIGHT, STAIRS, BOSS, FOUNT }
    private enum Mode { EXPLORE, BATTLE, LOOT, HERO, LEVEL, INVENTORY, OVER }

    public static final int FOUNT_HEAL_PCT = 30;

    private final JLabel header = new JLabel("", SwingConstants.CENTER);
    private final JLabel[][] cells = new JLabel[SIZE][SIZE];
    private final Color[][] cellAlpha = new Color[SIZE][SIZE];
    private static class BgGrid extends JPanel {
        private BufferedImage bgImg;
        private final Color[][] cellAlpha;
        BgGrid(Color[][] cellAlpha) {
            super(new GridLayout(SIZE, SIZE, 3, 3));
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
    private final BgGrid grid = new BgGrid(cellAlpha);
    private final JLabel eventLabel = new JLabel("", SwingConstants.CENTER);
    private final JPanel actions = new JPanel();
    private final JPanel mapWrap = new JPanel(new GridBagLayout());
    private final JPanel invOverlay = new JPanel(new BorderLayout(6, 6));
    private final JLabel invText = new JLabel("", SwingConstants.LEFT);
    private final JButton invBack = new JButton("Wroc (i/Esc)");
    private final JPanel lootOverlay = new JPanel(new BorderLayout(6, 6));
    private final JLabel lootTitle = new JLabel("", SwingConstants.CENTER);
    private final JPanel lootBox = new JPanel();
    private final Random rng = new Random();

    private RunState run;
    private Tile[][] map;
    private List<Enemy>[][] foes;
    private Enemy boss;
    private boolean hasKey;
    private int keyR = -1;
    private int keyC = -1;
    private int pr = SIZE - 1;
    private int pc = 0;
    private Mode mode = Mode.EXPLORE;

    private java.util.List<Enemy> pendingFoes;
    private boolean pendingBoss;
    private boolean pendingKey;
    private int pendingR;
    private int pendingC;
    private int pendingOldR;
    private int pendingOldC;
    private int invIdx;
    private LootTable.Item[] pendingDrops;
    private LootTable.Item pendingItem;
    private JButton[] lootButtons = new JButton[0];
    private JButton[] heroButtons = new JButton[0];
    private JButton[] lvlButtons = new JButton[0];
    private final List<Hero> levelQueue = new ArrayList<>();
    private int levelIdx;
    private Runnable afterLevels = () -> {};

    private Listener listener = (r, c, d) -> {};
    private BattleStarter battleStarter = (r, f, b, cb) -> {};

    public DungeonPanel() {
        super(new BorderLayout());
        setBackground(new Color(20, 20, 25));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setBorder(BorderFactory.createEmptyBorder(8, 0, 4, 0));
        add(header, BorderLayout.NORTH);

        grid.setBackground(Color.BLACK);
        grid.setBorder(BorderFactory.createEmptyBorder(4, 24, 4, 24));
        grid.setOpaque(false);
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                JLabel l = new JLabel("", SwingConstants.CENTER);
                l.setOpaque(false);
                l.setFont(new Font("SansSerif", Font.BOLD, 16));
                l.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 70, 80)));
                cells[r][c] = l;
                grid.add(l);
            }
        }
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mapWrap.add(grid, gbc);

        invOverlay.setBackground(new Color(12, 12, 18));
        invOverlay.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 165, 32), 2),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        invOverlay.setPreferredSize(new Dimension(600, 300));
        invText.setForeground(Color.WHITE);
        invText.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        invText.setVerticalAlignment(SwingConstants.TOP);
        invOverlay.add(invText, BorderLayout.CENTER);
        Keys.noEnter(invBack);
        invBack.addActionListener(e -> closeInv());
        JPanel invBottom = new JPanel();
        invBottom.setBackground(new Color(12, 12, 18));
        invBottom.add(invBack);
        invOverlay.add(invBottom, BorderLayout.SOUTH);
        invOverlay.setVisible(false);
        GridBagConstraints obc = new GridBagConstraints();
        obc.gridx = 0;
        obc.gridy = 0;
        obc.anchor = GridBagConstraints.CENTER;
        mapWrap.add(invOverlay, obc);

        lootOverlay.setBackground(new Color(12, 12, 18));
        lootOverlay.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 165, 32), 2),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        lootOverlay.setPreferredSize(new Dimension(520, 340));
        lootTitle.setForeground(new Color(255, 230, 150));
        lootTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        lootOverlay.add(lootTitle, BorderLayout.NORTH);
        lootBox.setLayout(new BoxLayout(lootBox, BoxLayout.Y_AXIS));
        lootBox.setBackground(new Color(12, 12, 18));
        lootOverlay.add(lootBox, BorderLayout.CENTER);
        lootOverlay.setVisible(false);
        mapWrap.add(lootOverlay, obc);
        add(mapWrap, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(new Color(20, 20, 25));
        bottom.setBorder(BorderFactory.createEmptyBorder(6, 24, 10, 24));
        eventLabel.setForeground(new Color(255, 230, 150));
        eventLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        eventLabel.setPreferredSize(new Dimension(100, 66));
        bottom.add(eventLabel, BorderLayout.NORTH);
        actions.setBackground(new Color(20, 20, 25));
        bottom.add(actions, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        setupKeys();
        showExploreActions();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setBattleStarter(BattleStarter battleStarter) {
        this.battleStarter = battleStarter;
    }

    public void startRun(java.util.List<Hero> party, Dungeon dungeon) {
        run = new RunState(party, dungeon);
        run.setFloor(1);
        for (Hero h : run.getParty()) {
            h.healFull();
        }
        invOverlay.setVisible(false);
        loadDungeonBg(dungeon);
        setEvent("Pietro 1/" + LAST_FLOOR + ". Klucz u losowego wroga. [i] = ekwipunek."  );
        generateFloor();
    }

    private void loadDungeonBg(Dungeon d) {
        String key = "bg/" + d.name().toLowerCase(java.util.Locale.ROOT);
        BufferedImage img = null;
        String[] exts = {".png", ".jpg", ".jpeg"};
        for (String ext : exts) {
            try {
                java.nio.file.Path p = java.nio.file.Paths.get(
                        System.getProperty("user.dir"), "assets", key + ext);
                if (java.nio.file.Files.exists(p)) {
                    img = javax.imageio.ImageIO.read(p.toFile());
                    break;
                }
            } catch (Exception ignored) {}
        }
        if (img == null) {
            try {
                java.nio.file.Path p = java.nio.file.Paths.get(
                        "C:\\JavaProjects\\TarotCrawlerRPG\\assets", key + ".png");
                if (java.nio.file.Files.exists(p)) {
                    img = javax.imageio.ImageIO.read(p.toFile());
                }
            } catch (Exception ignored) {}
        }
        grid.setBgImg(img);
    }

    private void generateFloor() {
        map = new Tile[SIZE][SIZE];
        foes = new ArrayList[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                map[r][c] = Tile.FLOOR;
            }
        }
        pr = SIZE - 1;
        pc = 0;
        hasKey = false;
        keyR = -1;
        keyC = -1;

        if (run.getFloor() >= LAST_FLOOR) {
            map[0][4] = Tile.BOSS;
            boss = EnemyTable.scale(EnemyTable.randomBoss(rng), run.getParty().size());
            scatter(Tile.FIGHT, 6);
        } else {
            map[0][SIZE - 1] = Tile.STAIRS;
            scatter(Tile.FIGHT, 12);
        }
        scatter(Tile.FOUNT, 2 + rng.nextInt(2));
        pickKeyBearer();
        mode = Mode.EXPLORE;
        invOverlay.setVisible(false);
        lootOverlay.setVisible(false);
        showExploreActions();
        updateHeader();
        render();
    }

    private void scatter(Tile t, int n) {
        int placed = 0;
        int guard = 0;
        while (placed < n && guard++ < 500) {
            int r = rng.nextInt(SIZE);
            int c = rng.nextInt(SIZE);
            if (map[r][c] != Tile.FLOOR || (r == pr && c == pc)) {
                continue;
            }
            map[r][c] = t;
            if (t == Tile.FIGHT) {
                int maxG = run.getFloor() < 4 ? 2 : run.getFloor() < 7 ? 3 : 4;
                int count = 1 + rng.nextInt(maxG);
                List<Enemy> group = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    group.add(EnemyTable.scale(
                            EnemyTable.forFloor(run.getDungeon(), run.getFloor(), rng),
                            run.getParty().size()));
                }
                foes[r][c] = group;
            }
            placed++;
        }
    }

    /** Klucz do schodow/bossa nosi losowy wrog na pietrze (nieoznaczony). */
    private void pickKeyBearer() {
        List<int[]> fights = new ArrayList<>();
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (map[r][c] == Tile.FIGHT) {
                    fights.add(new int[]{r, c});
                }
            }
        }
        if (!fights.isEmpty()) {
            int[] k = fights.get(rng.nextInt(fights.size()));
            keyR = k[0];
            keyC = k[1];
        }
    }

    private void setupKeys() {
    }

    /** Ruch z centralnego dispatchera (MainFrame): tylko strzalki. */
    public void handleArrows(int dx, int dy) {
        if (run == null) {
            return;
        }
        if (mode == Mode.LOOT || mode == Mode.HERO || mode == Mode.LEVEL) {
            cycleFocus((dx + dy) > 0 ? 1 : -1);
        } else if (mode == Mode.INVENTORY) {
            invIdx = (invIdx + ((dx + dy) > 0 ? 1 : -1) + run.getParty().size()) % run.getParty().size();
            refreshInv();
        } else {
            step(dx, dy);
        }
    }

    /** Podglad inventory+statystyk na klawisz i (tylko eksploracja). */
    public void toggleInv() {
        if (!isShowing() || run == null) {
            return;
        }
        if (mode == Mode.INVENTORY) {
            closeInv();
        } else if (mode == Mode.EXPLORE) {
            openInv();
        }
    }

    private void openInv() {
        mode = Mode.INVENTORY;
        invIdx = 0;
        refreshInv();
        invOverlay.setVisible(true);
        updateHeader();
        render();
        SwingUtilities.invokeLater(invBack::requestFocusInWindow);
    }

    private void refreshInv() {
        java.util.List<Hero> party = run.getParty();
        if (party.isEmpty()) {
            return;
        }
        invIdx = Math.max(0, Math.min(invIdx, party.size() - 1));
        invText.setText(buildInvHtml(party.get(invIdx), invIdx, party.size()));
    }

    private void closeInv() {
        invOverlay.setVisible(false);
        mode = Mode.EXPLORE;
        showExploreActions();
        updateHeader();
        render();
    }

    /** Esc jako wstecz: zamyka podglad, wraca do wyboru lootu, konczy poziom. */
    public void handleEsc() {
        if (!isShowing() || run == null) {
            return;
        }
        switch (mode) {
            case INVENTORY:
                closeInv();
                break;
            case HERO:
                mode = Mode.LOOT;
                setEvent("<html>Wybierz JEDEN loot - strzalki + Spacja:</html>");
                showLoot();
                updateHeader();
                render();
                break;
            case LEVEL:
                nextLevelUp();
                break;
            default:
                break;
        }
    }

    private String buildInvHtml(Hero h, int idx, int total) {
        StringBuilder sb = new StringBuilder("<html><center><b>EKWIPUNEK ["
                + (idx + 1) + "/" + total + "] - strzalki zmieniaja postac</b><br><br>");
        sb.append("<b>").append(h.getTemplate().getSymbol()).append(" ")
                .append(h.getTemplate().getName()).append("</b> lvl ").append(h.getLevel())
                .append(" exp ").append(h.getExp()).append("<br>"
                + "HP ").append(h.getHpCur()).append("/").append(h.maxHp())
                .append(" ATK ").append(h.power()).append(" DEF ").append(h.defense())
                .append(" SPD ").append(h.speed()).append(" MANA ").append(h.maxMana())
                .append(" UN ").append(h.dodge()).append("% MG +")
                .append((int) ((h.spellMult() - 1) * 100)).append("%");
        if (h.getPool() > 0) {
            sb.append(" <b>[").append(h.getPool()).append(" PKT]</b>");
        }
        sb.append("<br>---------------<br>");
        for (Gear.Slot slot : Gear.Slot.values()) {
            Gear g = h.getGear().get(slot);
            sb.append(slotLabel(slot)).append(": ")
                    .append(g == null ? "--" : g.describe()).append("<br>");
        }
        return sb.toString() + "</center></html>";
    }

    private static String slotLabel(Gear.Slot s) {
        if (s == Gear.Slot.HAND_L || s == Gear.Slot.HAND_R) {
            return "Reka";
        }
        return s.getLabel();
    }

    private void cycleFocus(int dir) {
        JButton[] arr = mode == Mode.HERO ? heroButtons : mode == Mode.LEVEL ? lvlButtons : lootButtons;
        if ((mode != Mode.LOOT && mode != Mode.HERO && mode != Mode.LEVEL) || arr.length == 0) {
            return;
        }
        int cur = -1;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].isFocusOwner()) {
                cur = i;
                break;
            }
        }
        int next = cur < 0 ? (dir > 0 ? 0 : arr.length - 1)
                : (cur + dir + arr.length) % arr.length;
        arr[next].requestFocusInWindow();
    }

    private void step(int dx, int dy) {
        if (run == null || mode != Mode.EXPLORE) {
            return;
        }
        int nr = pr + dy;
        int nc = pc + dx;
        if (nr < 0 || nr >= SIZE || nc < 0 || nc >= SIZE) {
            return;
        }
        int oldR = pr;
        int oldC = pc;
        pr = nr;
        pc = nc;
        Tile t = map[pr][pc];
        switch (t) {
            case FIGHT:
                pendingFoes = new ArrayList<>(foes[pr][pc]);
                pendingBoss = false;
                GameLog.debug("wszedlem na walke, wrogow=" + pendingFoes.size()
                        + " pietro=" + run.getFloor());
                pendingKey = (pr == keyR && pc == keyC);
                pendingR = pr;
                pendingC = pc;
                pendingOldR = oldR;
                pendingOldC = oldC;
                mode = Mode.BATTLE;
                setEvent("Walka! " + pendingFoes.size() + " wrogow...");
                updateHeader();
                render();
                battleStarter.start(run, new ArrayList<>(pendingFoes), false, this::onBattleResult);
                return;
            case STAIRS:
                if (!hasKey) {
                    setEvent("Zamkniete! Klucz wypadnie z losowego pokonanego wroga.");
                    pr = oldR;
                    pc = oldC;
                    render();
                    return;
                }
                run.setFloor(run.getFloor() + 1);
                setEvent(run.getFloor() >= LAST_FLOOR
                        ? "Pietro 10! Boss czeka (czerwony kafel, tez zamkniety na klucz)."
                        : "Schodzisz na pietro " + run.getFloor() + " / " + LAST_FLOOR
                        + ". Znajdz klucz u losowego wroga.");
                generateFloor();
                return;
            case BOSS:
                if (!hasKey) {
                    setEvent("Legowisko zamkniete! Klucz wypadnie z losowego pokonanego wroga.");
                    pr = oldR;
                    pc = oldC;
                    render();
                    return;
                }
                java.util.List<Enemy> bossGroup = new ArrayList<>();
                bossGroup.add(boss);
                pendingFoes = bossGroup;
                pendingBoss = true;
                pendingKey = false;
                pendingR = pr;
                pendingC = pc;
                pendingOldR = oldR;
                pendingOldC = oldC;
                mode = Mode.BATTLE;
                setEvent("BOSS: " + boss.getName() + "!");
                updateHeader();
                render();
                battleStarter.start(run, new ArrayList<>(pendingFoes), true, this::onBattleResult);
                return;
            case FOUNT:
                for (Hero h : run.getParty()) {
                    h.healPercent(FOUNT_HEAL_PCT);
                }
                map[pr][pc] = Tile.FLOOR;
                setEvent("Kapliczka! Drużyna regeneruje " + FOUNT_HEAL_PCT + "% HP (wskrzesza).");
                break;
            default:
                break;
        }
        updateHeader();
        render();
    }

    private void onBattleResult(BattlePanel.Result result) {
        if (result == BattlePanel.Result.FLEE) {
            mode = Mode.EXPLORE;
            pr = pendingOldR;
            pc = pendingOldC;
            setEvent("Uciekliscie z walki.");
            showExploreActions();
            updateHeader();
            render();
            return;
        }
        if (result == BattlePanel.Result.LOSE) {
            mode = Mode.OVER;
            setEvent("<html>Porazka. Poziomy zostaja, sprzet przepada, exp do 70%.</html>");
            showOk("Wroc do lobby (Spacja)", () -> listener.onDungeonEnd(run, false, true));
            updateHeader();
            render();
            return;
        }
        java.util.List<Hero> party = run.getParty();
        int total = 0;
        Enemy best = pendingFoes.get(0);
        for (Enemy e : pendingFoes) {
            total += e.getExp();
            if (e.getExp() > best.getExp()) {
                best = e;
            }
        }
        int share = (total + party.size() - 1) / party.size();
        StringBuilder ups = new StringBuilder();
        for (Hero h : party) {
            int gained = h.addExp(share);
            if (gained > 0) {
                ups.append(h.getTemplate().getName()).append(" ->").append(h.getLevel()).append("! ");
            }
        }
        String msg = "Wygrana! +" + share + " exp dla kazdego."
                + (ups.length() > 0 ? " AWANS: " + ups : "");
        if (pendingBoss) {
            mode = Mode.OVER;
            run.setLastBossId(best.getId());
            setEvent("<html>" + msg + "<br><b>Loch wyczyszczony!</b></html>");
            startLevelUps(() -> {
                mode = Mode.OVER;
                showOk("Odbierz loch (Spacja)", () -> listener.onDungeonEnd(run, true, false));
            });
        } else {
            if (pendingKey) {
                hasKey = true;
                keyR = -1;
                keyC = -1;
            }
            pendingDrops = LootTable.roll(best, run.getDungeon(), rng);
            mode = Mode.LOOT;
                setEvent("<html>" + msg + (pendingKey ? "<br><b>Jeden z wrogow upuscil KLUCZ!</b>" : "")
                        + "<br>Wybierz JEDEN loot - strzalki + Spacja:</html>");
            showLoot();
        }
        updateHeader();
        render();
    }

    private void pickLoot(int i) {
        if (mode != Mode.LOOT) {
            return;
        }
        if (i < 0 || i >= pendingDrops.length) {
            return;
        }
        pendingItem = pendingDrops[i];
        if (run.getParty().size() <= 1) {
            applyGearTo(0);
            return;
        }
        mode = Mode.HERO;
        setEvent("Komu dac: " + pendingItem.describe() + "? (strzalki + Spacja)");
        showHeroPick();
        updateHeader();
        render();
    }

    private void showHeroPick() {
        lootTitle.setText("Komu dac: " + pendingItem.describe() + "?");
        lootBox.removeAll();
        java.util.List<Hero> party = run.getParty();
        heroButtons = new JButton[party.size()];
        for (int i = 0; i < party.size(); i++) {
            final int idx = i;
            Hero h = party.get(i);
            JButton b = bigButton(h.getTemplate().getSymbol() + " " + h.getTemplate().getName()
                    + " lvl " + h.getLevel());
            b.setFont(new Font("SansSerif", Font.PLAIN, 13));
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.setMaximumSize(new Dimension(440, 40));
            zoomOnFocus(b);
            b.addActionListener(e -> pickHero(idx));
            heroButtons[i] = b;
            lootBox.add(b);
        }
        lootBox.revalidate();
        lootOverlay.revalidate();
        lootOverlay.repaint();
        refreshActions();
        SwingUtilities.invokeLater(() -> heroButtons[0].requestFocusInWindow());
    }

    private void pickHero(int i) {
        if (mode != Mode.HERO) {
            return;
        }
        if (i < 0 || i >= run.getParty().size()) {
            return;
        }
        applyGearTo(i);
    }

    private void applyGearTo(int heroIdx) {
        Hero h = run.getParty().get(heroIdx);
        Gear old = h.equip(pendingItem.gear);
        Gear.Slot at = h.slotOf(pendingItem.gear);
        String bonus = " -> [" + (at == null ? "?" : at.getLabel()) + "]";
        if (old != null) {
            bonus += " (zamienia " + old.name + ")";
        }
        map[pendingR][pendingC] = Tile.FLOOR;
        foes[pendingR][pendingC] = null;
        mode = Mode.EXPLORE;
        setEvent("Wzieto: " + pendingItem.describe() + " -> " + h.getTemplate().getName() + bonus);
        lootOverlay.setVisible(false);
        startLevelUps(this::backToExplore);
    }

    private void backToExplore() {
        mode = Mode.EXPLORE;
        showExploreActions();
        updateHeader();
        render();
    }

    private void startLevelUps(Runnable after) {
        levelQueue.clear();
        for (Hero h : run.getParty()) {
            if (h.getPool() > 0) {
                levelQueue.add(h);
            }
        }
        levelIdx = 0;
        if (levelQueue.isEmpty()) {
            after.run();
        } else {
            afterLevels = after;
            showLevelUp();
        }
    }

    private void showLevelUp() {
        mode = Mode.LEVEL;
        Hero h = levelQueue.get(levelIdx);
        setEvent("<html>AWANS! <b>" + h.getTemplate().getSymbol() + " " + h.getTemplate().getName()
                + "</b> lvl " + h.getLevel() + " - rozdziel " + h.getPool()
                + " pkt (strzalki + Spacja):</html>");
        actions.removeAll();
        actions.setLayout(new FlowLayout());
        Hero.Attr[] attrs = Hero.Attr.values();
        lvlButtons = new JButton[attrs.length + 1];
        for (int i = 0; i < attrs.length; i++) {
            final Hero.Attr a = attrs[i];
            JButton b = bigButton(a.getLabel() + " " + h.attrValue(a) + " (" + a.getDesc() + ")");
            b.setFont(new Font("SansSerif", Font.PLAIN, 11));
            zoomOnFocus(b);
            b.addActionListener(e -> {
                if (h.spendPoint(a)) {
                    showLevelUp();
                }
            });
            lvlButtons[i] = b;
            actions.add(b);
        }
        JButton done = bigButton("Gotowe");
        done.addActionListener(e -> nextLevelUp());
        lvlButtons[attrs.length] = done;
        actions.add(done);
        refreshActions();
        updateHeader();
        render();
        SwingUtilities.invokeLater(() -> lvlButtons[0].requestFocusInWindow());
    }

    private void nextLevelUp() {
        levelIdx++;
        if (levelIdx >= levelQueue.size()) {
            Runnable a = afterLevels;
            afterLevels = () -> {};
            a.run();
        } else {
            showLevelUp();
        }
    }

    private void discardLoot() {
        if (mode != Mode.LOOT) {
            return;
        }
        map[pendingR][pendingC] = Tile.FLOOR;
        foes[pendingR][pendingC] = null;
        mode = Mode.EXPLORE;
        setEvent("Porzucono loot.");
        lootOverlay.setVisible(false);
        startLevelUps(this::backToExplore);
    }

    private void setEvent(String html) {
        eventLabel.setText(html.startsWith("<html>") ? html : "<html>" + html + "</html>");
    }

    private JButton bigButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 16));
        Keys.noEnter(b);
        return b;
    }

    /** Powiekszenie aktualnie wybranego (focus) - strzalki przesuwaja powiekszenie. */
    private static void zoomOnFocus(JButton b) {
        Font normal = b.getFont();
        Font big = normal.deriveFont(Font.BOLD, normal.getSize() + 5);
        b.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                b.setFont(big);
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                b.setFont(normal);
            }
        });
    }

    private void refreshActions() {
        actions.revalidate();
        actions.repaint();
    }

    private void showExploreActions() {
        actions.removeAll();
        actions.setLayout(new FlowLayout());
        JButton exit = bigButton("Ucieknij z lochu");
        exit.addActionListener(e -> {
            if (mode == Mode.EXPLORE && run != null) {
                listener.onDungeonEnd(run, false, false);
            }
        });
        actions.add(exit);
        refreshActions();
        SwingUtilities.invokeLater(exit::requestFocusInWindow);
    }

    private void showLoot() {
        lootTitle.setText("Wybierz 1 loot:");
        lootBox.removeAll();
        lootButtons = new JButton[pendingDrops.length + 1];
        for (int i = 0; i < pendingDrops.length; i++) {
            final int idx = i;
            JButton b = bigButton(pendingDrops[i].describe());
            b.setFont(new Font("SansSerif", Font.PLAIN, 13));
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.setMaximumSize(new Dimension(440, 40));
            String ic = slotIconKey(pendingDrops[i].gear);
            if (Assets.exists(ic)) {
                b.setIcon(Assets.icon(ic, 28));
            }
            zoomOnFocus(b);
            b.addActionListener(e -> pickLoot(idx));
            lootButtons[i] = b;
            lootBox.add(b);
        }
        JButton skip = bigButton("Pomin loot");
        skip.setFont(new Font("SansSerif", Font.PLAIN, 13));
        skip.setAlignmentX(Component.CENTER_ALIGNMENT);
        skip.setMaximumSize(new Dimension(440, 40));
        zoomOnFocus(skip);
        skip.addActionListener(e -> discardLoot());
        lootButtons[pendingDrops.length] = skip;
        lootBox.add(skip);
        lootOverlay.setVisible(true);
        lootBox.revalidate();
        lootOverlay.revalidate();
        lootOverlay.repaint();
        showHint("Wybierz w okienku powyżej.");
        refreshActions();
        SwingUtilities.invokeLater(() -> lootButtons[lootButtons.length - 1].requestFocusInWindow());
    }

    private static String slotIconKey(Gear g) {
        if (g.isWand()) {
            return "icons/wand";
        }
        switch (g.slot) {
            case HELM: return "icons/helm";
            case ARMOR: return "icons/armor";
            case BOOTS: return "icons/boots";
            case JEWELRY: return "icons/amulet";
            default:
                return g.name.startsWith("Miecz") ? "icons/sword" : "icons/shield";
        }
    }

    private void showHint(String text) {
        actions.removeAll();
        actions.setLayout(new FlowLayout());
        JLabel hint = new JLabel(text);
        hint.setForeground(Color.GRAY);
        actions.add(hint);
    }

    private void showOk(String text, Runnable onOk) {
        actions.removeAll();
        actions.setLayout(new FlowLayout());
        JButton ok = bigButton(text);
        ok.addActionListener(e -> onOk.run());
        actions.add(ok);
        refreshActions();
        SwingUtilities.invokeLater(ok::requestFocusInWindow);
    }

    private void updateHeader() {
        if (run == null) {
            return;
        }
        header.setText(run.getDungeon().getName() + "  |  Pietro " + run.getFloor() + "/" + LAST_FLOOR
                + "  |  " + run.partySymbols()
                + (hasKey ? "  |  KLUCZ" : ""));
    }

    @SuppressWarnings("unchecked")
    private void render() {
        if (map == null) {
            return;
        }
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                JLabel l = cells[r][c];
                cellAlpha[r][c] = null;
                if (r == pr && c == pc) {
                    l.setIcon(Assets.icon("hero/"
                            + run.getLeader().getTemplate().name().toLowerCase(java.util.Locale.ROOT), 32));
                    l.setText("");
                    cellAlpha[r][c] = new Color(60, 60, 80, 140);
                    l.setToolTipText(run.getLeader().getTemplate().getName());
                    continue;
                }
                switch (map[r][c]) {
                    case FIGHT: {
                        List<Enemy> g = foes[r][c];
                        int n = g == null ? 0 : g.size();
                        l.setIcon(null);
                        l.setText(n > 1 ? n + "x!" : "!");
                        l.setForeground(Color.WHITE);
                        cellAlpha[r][c] = new Color(178, 34, 34, 180);
                        if (g == null || g.isEmpty()) {
                            l.setToolTipText("Wrogowie");
                        } else {
                            StringBuilder tt = new StringBuilder();
                            for (Enemy e : g) {
                                if (tt.length() > 0) {
                                    tt.append(", ");
                                }
                                tt.append(e.getName());
                            }
                            l.setToolTipText(tt.toString());
                        }
                        break;
                    }
                    case STAIRS:
                        l.setIcon(null);
                        l.setText("v");
                        l.setForeground(Color.WHITE);
                        cellAlpha[r][c] = hasKey ? new Color(70, 130, 180, 160) : new Color(70, 70, 80, 100);
                        l.setToolTipText(hasKey ? "Schody w dol"
                                : "Zamkniete - klucz wypadnie z losowego wroga");
                        break;
                    case BOSS:
                        l.setIcon(null);
                        l.setText("B");
                        l.setForeground(Color.WHITE);
                        cellAlpha[r][c] = hasKey ? new Color(220, 20, 20, 180) : new Color(120, 30, 30, 100);
                        l.setToolTipText((boss == null ? "Boss" : boss.getName())
                                + (hasKey ? "" : " (zamkniete - klucz wypadnie z losowego wroga)"));
                        break;
                    case FOUNT:
                        l.setIcon(null);
                        l.setText("+");
                        l.setForeground(Color.BLACK);
                        cellAlpha[r][c] = new Color(60, 179, 113, 150);
                        l.setToolTipText("Kapliczka: +" + FOUNT_HEAL_PCT + "% HP");
                        break;
                    default:
                        l.setIcon(null);
                        l.setText("");
                        cellAlpha[r][c] = new Color(40, 40, 50, 60);
                        l.setToolTipText(null);
                        break;
                }
            }
        }
        grid.repaint();
    }
}
