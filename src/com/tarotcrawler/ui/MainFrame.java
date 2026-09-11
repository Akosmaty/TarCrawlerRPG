package com.tarotcrawler.ui;

import com.tarotcrawler.model.Dungeon;
import com.tarotcrawler.model.Hero;
import com.tarotcrawler.model.HeroClass;
import com.tarotcrawler.model.RunState;
import com.tarotcrawler.model.SaveData;
import com.tarotcrawler.model.Spell;
import com.tarotcrawler.util.Assets;
import com.tarotcrawler.util.GameLog;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainFrame extends JFrame {

    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);
    private final List<HeroClass> selectedParty =
            new ArrayList<>(Arrays.asList(HeroClass.GLUPIEC, HeroClass.MAG));
    private final JLabel lobbyHeroLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel lobbyStatus = new JLabel(" ", SwingConstants.CENTER);
    private LobbyPanel lobby;
    private DungeonPanel dungeon;
    private BattlePanel battle;
    private JPanel selectPanel;
    private JPanel menuPanel;
    private final List<JComponent> menuItems = new ArrayList<>();
    private final List<JComponent> selectItems = new ArrayList<>();
    private final List<JButton> lobbyButtons = new ArrayList<>();
    private final SaveData store = SaveData.load();
    private final Set<Dungeon> cleared = EnumSet.noneOf(Dungeon.class);
    private final Set<String> unlocked = new HashSet<>();
    private final Map<HeroClass, Hero> roster = new EnumMap<>(HeroClass.class);

    public MainFrame() {
        super("Tarot Crawler RPG - prototyp");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        unlocked.addAll(store.unlocked);
        cleared.addAll(store.cleared);
        if (!store.party.isEmpty()) {
            selectedParty.clear();
            for (String n : store.party) {
                try {
                    HeroClass hc = HeroClass.valueOf(n);
                    if ((hc.isStarter() || unlocked.contains(n)) && !selectedParty.contains(hc)) {
                        selectedParty.add(hc);
                    }
                } catch (IllegalArgumentException ignored) {
                }
            }
            while (selectedParty.size() > 4) {
                selectedParty.remove(selectedParty.size() - 1);
            }
            if (selectedParty.isEmpty()) {
                selectedParty.add(HeroClass.GLUPIEC);
            }
        }

        root.add(buildMenu(), "MENU");
        showSelect();
        root.add(buildLobby(), "LOBBY");
        dungeon = new DungeonPanel();
        dungeon.setListener(this::onDungeonEnd);
        root.add(dungeon, "DUNGEON");
        battle = new BattlePanel();
        root.add(battle, "BATTLE");
        dungeon.setBattleStarter((run, foes, isBoss, cb) -> {
            GameLog.info("start bitwy: " + foes.size() + " wrogow, boss=" + isBoss);
            battle.startBattle(run, foes, isBoss, result -> {
                GameLog.info("koniec bitwy: " + result);
                cb.accept(result);
                cards.show(root, "DUNGEON");
            });
            cards.show(root, "BATTLE");
        });
        add(root);
        setupGlobalKeys();
        showMenu();
    }

    /** Jeden dispatcher klawiatury na cale okno: TYLKO strzalki + spacja. */
    private void setupGlobalKeys() {
        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();
        int[] codes = {java.awt.event.KeyEvent.VK_UP, java.awt.event.KeyEvent.VK_DOWN,
                java.awt.event.KeyEvent.VK_LEFT, java.awt.event.KeyEvent.VK_RIGHT};
        int[] dxs = {0, 0, -1, 1};
        int[] dys = {-1, 1, 0, 0};
        for (int i = 0; i < codes.length; i++) {
            final int dx = dxs[i];
            final int dy = dys[i];
            final int dir = dx != 0 ? dx : dy;
            String n = "G_AR" + i;
            im.put(KeyStroke.getKeyStroke(codes[i], 0), n);
            am.put(n, new AbstractAction() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    dispatchArrow(dx, dy, dir);
                }
            });
        }
        im.put(KeyStroke.getKeyStroke("SPACE"), "G_SP");
        am.put("G_SP", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispatchSpace();
            }
        });
        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, 0), "G_I");
        am.put("G_I", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (dungeon.isShowing()) {
                    dungeon.toggleInv();
                }
            }
        });
        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0), "G_ESC");
        am.put("G_ESC", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (battle.isShowing()) {
                    battle.handleEsc();
                } else if (dungeon.isShowing()) {
                    dungeon.handleEsc();
                } else if (selectPanel != null && selectPanel.isShowing()) {
                    showMenu();
                }
            }
        });
    }

    private void dispatchArrow(int dx, int dy, int dir) {
        GameLog.debug("strzalka dx=" + dx + " dy=" + dy
                + " menu=" + menuPanel.isShowing()
                + " select=" + (selectPanel != null && selectPanel.isShowing())
                + " lobby=" + lobby.isShowing()
                + " dungeon=" + dungeon.isShowing()
                + " battle=" + battle.isShowing());
        if (menuPanel.isShowing()) {
            Keys.cycle(menuItems, dir);
        } else if (selectPanel != null && selectPanel.isShowing()) {
            Keys.cycle(selectItems, dir);
        } else if (lobby.isShowing()) {
            if (lobbyButtonFocused()) {
                Keys.cycle(lobbyFocusItems(), dir);
            } else {
                lobby.move(dx, dy);
            }
        } else if (dungeon.isShowing()) {
            dungeon.handleArrows(dx, dy);
        } else if (battle.isShowing()) {
            battle.handleArrows(dx, dy);
        }
    }

    private void dispatchSpace() {
        if (lobby.isShowing()) {
            Component f = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            if (f instanceof JButton) {
                return;
            }
            if (!lobbyButtons.isEmpty()) {
                lobbyButtons.get(0).requestFocusInWindow();
            }
        } else if (battle.isShowing()) {
            battle.handleSpace();
        }
    }

    private boolean lobbyButtonFocused() {
        Component f = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        return f != null && lobbyButtons.contains(f);
    }

    private List<JComponent> lobbyFocusItems() {
        return new ArrayList<>(lobbyButtons);
    }

    private Hero getHero(HeroClass hc) {
        Hero h = roster.get(hc);
        if (h == null) {
            h = new Hero(hc);
            int[] le = store.heroes.get(hc.name());
            if (le != null) {
                h.setLevel(le[0]);
                h.setExp(le[1]);
                if (le.length > 2) {
                    h.addPool(le[2]);
                } else if (le[0] > 1) {
                    h.addPool((le[0] - 1) * Hero.POINTS_PER_LEVEL);
                }
            }
            List<String> sp = store.spells.get(hc.name());
            if (sp != null) {
                h.setSpells(sp);
            }
            roster.put(hc, h);
        }
        return h;
    }

    private void collectSave() {
        store.unlocked.clear();
        store.unlocked.addAll(unlocked);
        store.cleared.clear();
        store.cleared.addAll(cleared);
        store.party.clear();
        for (HeroClass hc : selectedParty) {
            store.party.add(hc.name());
        }
        for (Map.Entry<HeroClass, Hero> e : roster.entrySet()) {
            Hero h = e.getValue();
            store.heroes.put(e.getKey().name(), new int[]{h.getLevel(), h.getExp(), h.getPool()});
            store.spells.put(e.getKey().name(), h.getSpells());
        }
        store.save();
        GameLog.info("zapisano gre");
    }

    private void refreshLobbyLabel() {
        StringBuilder sym = new StringBuilder();
        for (HeroClass hc : selectedParty) {
            sym.append(hc.getSymbol()).append(getHero(hc).getLevel()).append(" ");
        }
        lobbyHeroLabel.setText("Druzyna: " + sym.toString().trim()
                + "  |  Lochy: " + cleared.size() + "/4");
    }

    private String teamText() {
        StringBuilder sym = new StringBuilder();
        for (HeroClass hc : selectedParty) {
            sym.append(hc.getSymbol()).append(getHero(hc).getLevel()).append(" ");
        }
        return "Druzyna (" + selectedParty.size() + "/4): " + sym + "- kliknij aby dodac/usunac";
    }

    private void showMenu() {
        if (menuPanel != null) {
            root.remove(menuPanel);
        }
        menuPanel = buildMenu();
        root.add(menuPanel, "MENU");
        cards.show(root, "MENU");
        focusFirst(menuItems);
    }

    private void enterLobby() {
        lobby.setHero(selectedParty.get(0));
        lobby.resetPlayer();
        refreshLobbyLabel();
        cards.show(root, "LOBBY");
        focusLobby();
    }

    private void showSelect() {
        GameLog.debug("ekran SELECT");
        if (selectPanel != null) {
            root.remove(selectPanel);
        }
        selectPanel = buildSelect();
        root.add(selectPanel, "SELECT");
        cards.show(root, "SELECT");
        focusFirst(selectItems);
    }

    private void enterDungeon(Dungeon d) {
        GameLog.info("wejscie do lochu " + d);
        List<Hero> heroes = new ArrayList<>();
        for (HeroClass hc : selectedParty) {
            heroes.add(getHero(hc));
        }
        dungeon.startRun(heroes, d);
        cards.show(root, "DUNGEON");
    }

    private void onDungeonEnd(RunState run, boolean wasCleared, boolean died) {
        GameLog.info("koniec lochu " + run.getDungeon() + " cleared=" + wasCleared + " died=" + died);
        for (Hero h : run.getParty()) {
            if (died) {
                h.cutExpToPercent(70);
            }
            h.clearGear();
            h.healFull();
        }
        if (wasCleared) {
            String status = "Wyczyszczono: " + run.getDungeon().getName()
                    + " (" + (cleared.contains(run.getDungeon()) ? cleared.size() : cleared.size() + 1) + "/4)";
            cleared.add(run.getDungeon());
            List<HeroClass> newUnlocks = new ArrayList<>();
            String bossId = run.getLastBossId();
            if (bossId != null) {
                try {
                    HeroClass hc = HeroClass.valueOf(bossId);
                    if (unlocked.add(bossId)) {
                        newUnlocks.add(hc);
                    }
                } catch (IllegalArgumentException ignored) {
                }
            }
            String dungeonUnlock = null;
            if (run.getDungeon() == Dungeon.MIECZE) {
                dungeonUnlock = HeroClass.CESARZ.name();
            } else if (run.getDungeon() == Dungeon.BULAWY) {
                dungeonUnlock = HeroClass.KAPLANKA.name();
            }
            if (dungeonUnlock != null && unlocked.add(dungeonUnlock)) {
                newUnlocks.add(HeroClass.valueOf(dungeonUnlock));
            }
            if (!newUnlocks.isEmpty()) {
                StringBuilder u = new StringBuilder("  ODLOKOWANO:");
                for (HeroClass hc : newUnlocks) {
                    u.append(" ").append(hc.getSymbol()).append(" ").append(hc.getName()).append(";");
                }
                status += u.toString();
            }
            if (cleared.size() == 4) {
                status += "  KONIEC GRY - wszystkie lochy!";
            }
            lobbyStatus.setText(status);
        } else if (died) {
            lobbyStatus.setText("Polegles w: " + run.getDungeon().getName()
                    + ". Poziomy zostaly, sprzet stracony, exp do 70%.");
        } else {
            lobbyStatus.setText("Opuszczono: " + run.getDungeon().getName() + ".");
        }
        collectSave();
        lobby.setCleared(cleared);
        enterLobby();
    }

    private JPanel buildMenu() {
        BgPanel p = new BgPanel(new GridBagLayout());
        p.setBackground(Color.BLACK);
        String bgPath = findAsset("bg/menu");
        if (bgPath != null) p.setBg(bgPath);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.insets = new Insets(8, 8, 8, 8);

        JLabel title = new JLabel("TAROT CRAWLER RPG");
        title.setFont(new Font("Serif", Font.BOLD, 42));
        title.setForeground(new Color(218, 165, 32));
        c.gridy = 0;
        p.add(title, c);

        JLabel sub = new JLabel("Sterowanie: tylko strzalki + Spacja");
        sub.setForeground(Color.GRAY);
        c.gridy = 1;
        p.add(sub, c);

        int row = 2;
        menuPanel = p;
        menuItems.clear();
        if (store.hasProgress()) {
            JButton cont = new JButton("Kontynuuj");
            cont.setFont(new Font("SansSerif", Font.BOLD, 18));
            Keys.noEnter(cont);
            cont.addActionListener(e -> {
                lobbyStatus.setText("Wczytano zapis.");
                enterLobby();
            });
            c.gridy = row++;
            p.add(cont, c);
            menuItems.add(cont);
        }
        JButton start = new JButton("Nowa gra");
        start.setFont(new Font("SansSerif", Font.BOLD, 18));
        Keys.noEnter(start);
        if (store.hasProgress()) {
            final boolean[] armed = {false};
            start.addActionListener(e -> {
                if (!armed[0]) {
                    armed[0] = true;
                    start.setText("Na pewno? Spacja = reset");
                } else {
                    resetAll();
                    showSelect();
                }
            });
        } else {
            start.addActionListener(e -> showSelect());
        }
        c.gridy = row++;
        p.add(start, c);

        JButton exit = new JButton("Wyjscie");
        Keys.noEnter(exit);
        exit.addActionListener(e -> System.exit(0));
        c.gridy = row++;
        p.add(exit, c);

        menuItems.add(start);
        menuItems.add(exit);
        return p;
    }

    private void resetAll() {
        unlocked.clear();
        cleared.clear();
        roster.clear();
        store.clearAll();
        store.save();
        selectedParty.clear();
        selectedParty.add(HeroClass.GLUPIEC);
        selectedParty.add(HeroClass.MAG);
        lobbyStatus.setText(" ");
        GameLog.info("reset zapisu - nowa gra");
    }

    private JPanel buildSelect() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.DARK_GRAY);

        JLabel header = new JLabel("Zbuduj druzyne (max 4) - strzalki + Spacja", SwingConstants.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 22));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        p.add(header, BorderLayout.NORTH);

        JLabel teamLabel = new JLabel(teamText(), SwingConstants.CENTER);
        teamLabel.setForeground(Color.YELLOW);
        teamLabel.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        p.add(teamLabel, BorderLayout.SOUTH);

        JPanel grid = new JPanel(new GridLayout(3, 3, 10, 10));
        grid.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
        grid.setBackground(Color.DARK_GRAY);

        selectItems.clear();
        for (HeroClass h : HeroClass.values()) {
            boolean open = h.isStarter() || unlocked.contains(h.name());
            JToggleButton b = new JToggleButton(open ? toHtml(h, getHero(h)) : lockedHtml(h));
            b.setFont(new Font("SansSerif", Font.PLAIN, 12));
            b.setSelected(selectedParty.contains(h));
            b.setEnabled(open);
            Keys.noEnter(b);
            b.setIcon(Assets.icon("hero/" + h.name().toLowerCase(java.util.Locale.ROOT), 48));
            b.setToolTipText(h.getName() + " - " + h.getPassive() + " [" + h.getRole() + "]");
            selectItems.add(b);
            if (open) {
                b.addActionListener(e -> {
                    GameLog.debug("klikniecie postaci " + h);
                    if (selectedParty.contains(h)) {
                        if (selectedParty.size() > 1) {
                            selectedParty.remove(h);
                        }
                    } else if (selectedParty.size() < 4) {
                        selectedParty.add(h);
                    }
                    b.setSelected(selectedParty.contains(h));
                    teamLabel.setText(teamText());
                });
            }
            grid.add(b);
        }
        p.add(grid, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setBackground(Color.DARK_GRAY);
        JButton back = new JButton("Wroc");
        Keys.noEnter(back);
        back.addActionListener(e -> showMenu());
        JButton next = new JButton("Dalej: Lobby");
        Keys.noEnter(next);
        next.addActionListener(e -> {
            collectSave();
            enterLobby();
        });
        bottom.add(back);
        bottom.add(next);
        selectItems.add(back);
        selectItems.add(next);
        JPanel south = new JPanel(new BorderLayout());
        south.setBackground(Color.DARK_GRAY);
        south.add(teamLabel, BorderLayout.NORTH);
        south.add(bottom, BorderLayout.SOUTH);
        p.add(south, BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildLobby() {
        BgPanel p = new BgPanel(new BorderLayout());
        p.setBackground(new Color(20, 20, 25));
        String bgPath = findAsset("bg/lobby");
        if (bgPath != null) p.setBg(bgPath);

        lobbyHeroLabel.setForeground(Color.WHITE);
        lobbyHeroLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        lobbyStatus.setForeground(new Color(255, 230, 150));
        lobbyStatus.setFont(new Font("SansSerif", Font.BOLD, 14));
        JPanel lobbyNorth = new JPanel(new GridLayout(2, 1));
        lobbyNorth.setOpaque(false);
        lobbyNorth.setBorder(BorderFactory.createEmptyBorder(12, 0, 4, 0));
        lobbyNorth.add(lobbyHeroLabel);
        lobbyNorth.add(lobbyStatus);
        p.add(lobbyNorth, BorderLayout.NORTH);

        lobby = new LobbyPanel();
        lobby.setOpaque(false);
        lobby.setOnEnter(this::enterDungeon);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        JLabel hint = new JLabel("Strzalki = ruch, Spacja = przyciski. Szare kafle na gorze to wejscia do 4 lochow.",
                SwingConstants.CENTER);
        hint.setForeground(Color.GRAY);
        hint.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        center.add(hint, BorderLayout.NORTH);
        center.add(lobby, BorderLayout.CENTER);
        p.add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        JButton back = new JButton("Zmien postac");
        Keys.noEnter(back);
        back.addActionListener(e -> showSelect());
        JButton saveBtn = new JButton("Zapisz gre");
        Keys.noEnter(saveBtn);
        saveBtn.addActionListener(e -> {
            collectSave();
            lobbyStatus.setText("Zapisano gre.");
        });
        bottom.add(back);
        bottom.add(saveBtn);
        lobbyButtons.clear();
        lobbyButtons.add(back);
        lobbyButtons.add(saveBtn);
        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    private void focusFirst(List<JComponent> items) {
        SwingUtilities.invokeLater(() -> {
            for (JComponent c : items) {
                if (c.isEnabled() && c.isVisible()) {
                    c.requestFocusInWindow();
                    break;
                }
            }
        });
    }

    private void focusLobby() {
        SwingUtilities.invokeLater(() -> lobby.requestFocusInWindow());
    }

    private static String lockedHtml(HeroClass h) {
        return "<html><center><font size='+3'>" + h.getSymbol() + "</font><br><b>" + h.getName() + "</b><br>"
                + "<i>ZAMKNIETA - pokonaj tego bossa</i></center></html>";
    }

    private static String toHtml(HeroClass h, Hero hero) {
        Spell sig = Spell.byId(h.getSignatureSpell());
        String pkt = hero.getPool() > 0 ? " <b>+" + hero.getPool() + "PKT!</b>" : "";
        return "<html><center><font size='+2'>" + h.getSymbol() + " " + h.getName() + "</font><br>"
                + "lvl " + hero.getLevel() + " SPD " + h.getSpd() + pkt + "<br>"
                + (sig == null ? "" : sig.shortDesc() + "<br>")
                + "HP" + h.getHp() + " ATK" + h.getAtk() + " DEF" + h.getDef()
                + "</center></html>";
    }

    private static String findAsset(String key) {
        String userDir = System.getProperty("user.dir");
        GameLog.info("findAsset: user.dir=" + userDir + " key=" + key);
        String[] exts = {".png", ".jpg", ".jpeg"};
        String[] bases = {userDir, "C:\\JavaProjects\\TarotCrawlerRPG"};
        for (String base : bases) {
            for (String ext : exts) {
                java.nio.file.Path p = java.nio.file.Paths.get(base, "assets", key + ext);
                if (java.nio.file.Files.exists(p)) {
                    GameLog.info("findAsset: OK " + p.toAbsolutePath());
                    return p.toAbsolutePath().toString();
                }
            }
        }
        GameLog.warn("findAsset: brak " + key);
        return null;
    }
}
