package com.tarotcrawler.ui;

import com.tarotcrawler.model.Enemy;
import com.tarotcrawler.model.Hero;
import com.tarotcrawler.model.HeroClass;
import com.tarotcrawler.model.RunState;
import com.tarotcrawler.model.Spell;
import com.tarotcrawler.util.Assets;
import com.tarotcrawler.util.GameLog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

/** Ekran walki: wrogowie na gorze, druzyna na dole, KOLEJKA po SPD z prawej,
 *  komendy Atak/Magia/Obrona/Leczenie/Ucieczka (pelna klawiatura), uniki, buffy. */
public class BattlePanel extends JPanel {

    public enum Result { WIN, FLEE, LOSE }

    private enum Mode { CMD, SPELL, T_FOE, T_ALLY, BUSY, DONE }

    private static class HState {
        Hero hero;
        int hp;
        int mana;
        boolean defend;
        boolean alive = true;
        int shieldDef;
        int shieldRounds;
        int dodgeBuff;
        int dodgeRounds;
    }

    private static class FState {
        Enemy foe;
        int hp;
        int maxHp;
        boolean alive = true;
    }

    private static final Color FOE_BG = new Color(120, 30, 30);
    private static final Color FOE_HI = new Color(205, 80, 80);
    private static final Color HERO_BG = new Color(40, 70, 40);
    private static final Color HERO_HI = new Color(75, 135, 75);

    private final Random rng = new Random();
    private final JPanel fieldWrap = new JPanel(new GridBagLayout());
    private final JPanel field = new JPanel(new GridLayout(2, 1, 0, 6));
    private final JPanel fxOverlay = new JPanel(null);
    private final JPanel foesRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 8));
    private final JPanel heroesRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 8));
    private final JPanel queuePanel = new JPanel();
    private final JLabel message = new JLabel(" ", SwingConstants.LEFT);
    private final JPanel menu = new JPanel(new CardLayout());
    private final JPanel mainMenu = new JPanel(new GridLayout(3, 2, 6, 6));
    private final JPanel spellMenu = new JPanel();

    private final List<HState> hs = new ArrayList<>();
    private final List<FState> fs = new ArrayList<>();
    private final List<Object> queue = new ArrayList<>();
    private final List<JPanel> foeBoxes = new ArrayList<>();
    private final List<JLabel> foeSymbols = new ArrayList<>();
    private final List<JProgressBar> foeBars = new ArrayList<>();
    private final List<JPanel> heroBoxes = new ArrayList<>();
    private final List<JLabel> heroSymbols = new ArrayList<>();
    private final List<JProgressBar> heroBars = new ArrayList<>();
    private final List<JLabel> heroMana = new ArrayList<>();
    private final List<JButton> cmdButtons = new ArrayList<>();
    private final List<JButton> spellButtons = new ArrayList<>();
    private int foeCursor;
    private int allyCursor;

    private RunState run;
    private double counterMult = 1.0;
    private boolean isBoss;
    private Consumer<Result> cb = r -> {};
    private Mode mode = Mode.BUSY;
    private int qIdx;
    private HState curH;
    private Spell pendingSpell;

    public BattlePanel() {
        super(new BorderLayout());
        setBackground(new Color(15, 15, 22));
        setFocusable(true);

        foesRow.setBackground(new Color(15, 15, 22));
        heroesRow.setBackground(new Color(15, 15, 22));
        field.setBackground(new Color(15, 15, 22));
        field.add(foesRow);
        field.add(heroesRow);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        fieldWrap.add(field, gbc);
        fxOverlay.setOpaque(false);
        GridBagConstraints obc = new GridBagConstraints();
        obc.gridx = 0;
        obc.gridy = 0;
        obc.anchor = GridBagConstraints.CENTER;
        fieldWrap.add(fxOverlay, obc);
        add(fieldWrap, BorderLayout.CENTER);

        queuePanel.setLayout(new BoxLayout(queuePanel, BoxLayout.Y_AXIS));
        queuePanel.setBackground(new Color(20, 20, 28));
        queuePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(218, 165, 32)), "KOLEJKA"));
        queuePanel.setPreferredSize(new Dimension(140, 100));
        add(queuePanel, BorderLayout.EAST);

        JPanel bottom = new JPanel(new BorderLayout(10, 0));
        bottom.setBackground(new Color(25, 25, 35));
        bottom.setBorder(BorderFactory.createEmptyBorder(8, 16, 10, 16));
        message.setForeground(new Color(255, 230, 150));
        message.setFont(new Font("SansSerif", Font.BOLD, 16));
        message.setPreferredSize(new Dimension(400, 90));
        bottom.add(message, BorderLayout.CENTER);

        mainMenu.setBackground(new Color(25, 25, 35));
        addCmdButton("Atak", this::doAttack);
        addCmdButton("Magia", this::doMagic);
        addCmdButton("Obrona", this::doDefend);
        addCmdButton("Leczenie", this::doHeal);
        addCmdButton("Ucieczka", this::doFlee);
        menu.add(mainMenu, "MAIN");
        spellMenu.setBackground(new Color(25, 25, 35));
        menu.add(spellMenu, "SPELL");
        menu.setPreferredSize(new Dimension(250, 150));
        bottom.add(menu, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);

        setupKeys();
    }

    private void addCmdButton(String text, Runnable action) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        Keys.noEnter(b);
        hlOnFocus(b);
        b.addActionListener(e -> action.run());
        mainMenu.add(b);
        cmdButtons.add(b);
    }

    /** Zolta obwodka na aktualnie wybranym przycisku (jak kursor celu). */
    private static void hlOnFocus(JButton b) {
        b.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        b.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                b.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                b.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
            }
        });
    }

    private void setupKeys() {
    }

    /** Ruch z centralnego dispatchera (MainFrame): tylko strzalki + spacja. */
    public void handleArrows(int dx, int dy) {
        nav(dx != 0 ? dx : dy);
    }

    public void handleSpace() {
        Component f = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        if (f instanceof JButton) {
            return;
        }
        if (mode == Mode.T_FOE) {
            selectFoe(foeCursor);
        } else if (mode == Mode.T_ALLY) {
            selectAlly(allyCursor);
        }
    }

    private void nav(int dir) {
        if (mode == Mode.CMD) {
            Keys.cycle(cmdButtons, dir);
        } else if (mode == Mode.SPELL) {
            Keys.cycle(spellButtons, dir);
        } else if (mode == Mode.T_FOE) {
            moveCursor(true, dir);
        } else if (mode == Mode.T_ALLY) {
            moveCursor(false, dir);
        }
    }

    private boolean targetAlive(boolean foe, int idx) {
        if (foe) {
            return idx >= 0 && idx < fs.size() && fs.get(idx).alive;
        }
        return idx >= 0 && idx < hs.size() && hs.get(idx).alive;
    }

    private void moveCursor(boolean foe, int dir) {
        int n = foe ? fs.size() : hs.size();
        int c = foe ? foeCursor : allyCursor;
        for (int k = 0; k < n; k++) {
            c = (c + dir + n) % n;
            if (targetAlive(foe, c)) {
                break;
            }
        }
        if (foe) {
            foeCursor = c;
        } else {
            allyCursor = c;
        }
        paintCursor();
    }

    private void paintCursor() {
        clearCursor();
        if (mode == Mode.T_FOE && targetAlive(true, foeCursor)) {
            foeBoxes.get(foeCursor).setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
            foeBoxes.get(foeCursor).setBackground(FOE_HI);
        } else if (mode == Mode.T_ALLY && targetAlive(false, allyCursor)) {
            heroBoxes.get(allyCursor).setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
            heroBoxes.get(allyCursor).setBackground(HERO_HI);
        }
    }

    private void clearCursor() {
        for (JPanel b : foeBoxes) {
            b.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
            b.setBackground(FOE_BG);
        }
        for (JPanel b : heroBoxes) {
            b.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
            b.setBackground(HERO_BG);
        }
    }

    /** Zaznaczenie aktywnej komendy (Atak/Magia/...) zolta obwodka jak u przeciwnikow. */
    private void markMenuButton(JButton b, List<JButton> all) {
        b.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                paintMenuCursor(all);
            }
            @Override
            public void focusLost(FocusEvent e) {
                paintMenuCursor(all);
            }
        });
    }

    private void paintMenuCursor(List<JButton> all) {
        for (JButton x : all) {
            if (x.isFocusOwner()) {
                x.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
            } else {
                x.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
            }
        }
    }

    private void focusPanel() {
        SwingUtilities.invokeLater(() -> requestFocusInWindow());
    }

    public void startBattle(RunState run, List<Enemy> foes, boolean boss, Consumer<Result> cb) {
        this.run = run;
        this.isBoss = boss;
        this.cb = cb;
        this.counterMult = run.counters(run.getDungeon()) ? 1.25 : 1.0;
        hs.clear();
        fs.clear();
        for (Hero h : run.getParty()) {
            HState s = new HState();
            s.hero = h;
            s.hp = Math.min(h.maxHp(), h.getHpCur());
            s.alive = s.hp > 0;
            h.resetMana();
            s.mana = h.getMana();
            hs.add(s);
        }
        for (Enemy e : foes) {
            FState s = new FState();
            s.foe = e;
            s.hp = e.getHp();
            s.maxHp = e.getHp();
            fs.add(s);
        }
        buildBoxes();
        showMenu("MAIN");
        if (boss && !foes.isEmpty()) {
            mode = Mode.BUSY;
            setMessage("<b>" + foes.get(0).getName() + "</b> zagradza droge!<br><i>"
                    + foes.get(0).getSpecial() + "</i>");
            Timer t = new Timer(1600, e -> newRound());
            t.setRepeats(false);
            t.start();
        } else {
            newRound();
        }
    }

    /** Omdlenie: ikona znika, zostaje X (ikony ignoruja kolor tekstu). */
    private static void faint(JLabel sym) {
        sym.setIcon(null);
        sym.setText("\u2715");
        sym.setForeground(Color.GRAY);
    }

    private String foeKey(Enemy foe) {
        if (foe.isBoss()) {
            return "boss/" + foe.getId().toLowerCase(java.util.Locale.ROOT);
        }
        if ("COURT".equals(foe.getId())) {
            String rank = foe.getName().split(" ")[0].toLowerCase(java.util.Locale.ROOT);
            return "foe/court-" + rank;
        }
        String suit = run.getDungeon().name().toLowerCase(java.util.Locale.ROOT);
        return "foe/pip-" + suit;
    }

    private String foeSymbol(Enemy foe) {
        if (foe.isBoss()) {
            return "\u2605";
        }
        String n = foe.getName();
        int sp = n.indexOf(' ');
        return sp > 0 ? n.substring(0, sp) : "?";
    }

    private String foeShort(FState s) {
        return foeSymbol(s.foe);
    }

    private void buildBoxes() {
        foesRow.removeAll();
        heroesRow.removeAll();
        foeBoxes.clear();
        foeSymbols.clear();
        foeBars.clear();
        heroBoxes.clear();
        heroSymbols.clear();
        heroBars.clear();
        heroMana.clear();
        for (int i = 0; i < fs.size(); i++) {
            final int idx = i;
            FState s = fs.get(i);
            JPanel box = new JPanel(new BorderLayout());
            box.setBackground(FOE_BG);
            box.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
            box.setPreferredSize(new Dimension(100, 115));
            JLabel name = new JLabel(s.foe.getName(), SwingConstants.CENTER);
            name.setForeground(Color.WHITE);
            name.setFont(new Font("SansSerif", Font.BOLD, 10));
            box.add(name, BorderLayout.NORTH);
            JLabel sym = new JLabel(foeSymbol(s.foe), SwingConstants.CENTER);
            sym.setForeground(Color.WHITE);
            sym.setFont(new Font("SansSerif", Font.BOLD, s.foe.isBoss() ? 40 : 26));
            box.add(sym, BorderLayout.CENTER);
            Assets.portrait(sym, foeKey(s.foe), foeSymbol(s.foe), 64, FOE_BG);
            int fmax = Math.max(1, s.maxHp);
            int fcur = Math.max(0, Math.min(s.hp, fmax));
            JProgressBar bar = new JProgressBar(0, fmax);
            bar.setValue(fcur);
            bar.setStringPainted(true);
            bar.setString(fcur + "/" + fmax);
            box.add(bar, BorderLayout.SOUTH);
            box.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectFoe(idx);
                }
            });
            box.setToolTipText(s.foe.statsLine());
            foeBoxes.add(box);
            foeSymbols.add(sym);
            foeBars.add(bar);
            foesRow.add(box);
        }
        for (int i = 0; i < hs.size(); i++) {
            final int idx = i;
            HState s = hs.get(i);
            JPanel box = new JPanel(new BorderLayout());
            box.setBackground(HERO_BG);
            box.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
            box.setPreferredSize(new Dimension(122, 135));
            JLabel name = new JLabel(s.hero.getTemplate().getSymbol() + " "
                    + s.hero.getTemplate().getName(), SwingConstants.CENTER);
            name.setForeground(Color.WHITE);
            name.setFont(new Font("SansSerif", Font.BOLD, 10));
            box.add(name, BorderLayout.NORTH);
            JLabel sym = new JLabel(s.hero.getTemplate().getSymbol(), SwingConstants.CENTER);
            sym.setForeground(Color.YELLOW);
            sym.setFont(new Font("SansSerif", Font.BOLD, 32));
            box.add(sym, BorderLayout.CENTER);
            Assets.portrait(sym, "hero/" + s.hero.getTemplate().name().toLowerCase(java.util.Locale.ROOT),
                    s.hero.getTemplate().getSymbol(), 64, HERO_BG);
            JPanel low = new JPanel(new GridLayout(3, 1));
            low.setBackground(new Color(40, 70, 40));
            int mhp = Math.max(1, s.hero.maxHp());
            int chp = Math.max(0, Math.min(s.hp, mhp));
            s.hp = chp;
            s.alive = chp > 0;
            JProgressBar bar = new JProgressBar(0, mhp);
            bar.setValue(chp);
            bar.setStringPainted(true);
            bar.setString(chp + "/" + mhp);
            low.add(bar);
            JLabel mana = new JLabel("Mana: " + s.mana + "/" + s.hero.maxMana(), SwingConstants.CENTER);
            mana.setForeground(new Color(150, 200, 255));
            mana.setFont(new Font("SansSerif", Font.PLAIN, 10));
            low.add(mana);
            JLabel spd = new JLabel("SPD " + s.hero.speed() + " UN " + s.hero.dodge() + "% MG +"
                    + (int) ((s.hero.spellMult() - 1) * 100) + "%",
                    SwingConstants.CENTER);
            spd.setFont(new Font("SansSerif", Font.PLAIN, 10));
            spd.setForeground(Color.LIGHT_GRAY);
            low.add(spd);
            box.add(low, BorderLayout.SOUTH);
            box.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectAlly(idx);
                }
            });
            heroBoxes.add(box);
            heroSymbols.add(sym);
            heroBars.add(bar);
            heroMana.add(mana);
            heroesRow.add(box);
        }
        revalidate();
        repaint();
    }

    private int speedOf(Object o) {
        if (o instanceof HState) {
            return ((HState) o).hero.speed();
        }
        return ((FState) o).foe.getSpd();
    }

    private void newRound() {
        for (HState s : hs) {
            s.defend = false;
            if (s.shieldRounds > 0 && --s.shieldRounds == 0) {
                s.shieldDef = 0;
            }
            if (s.dodgeRounds > 0 && --s.dodgeRounds == 0) {
                s.dodgeBuff = 0;
            }
        }
        queue.clear();
        for (HState s : hs) {
            if (s.alive) {
                queue.add(s);
            }
        }
        for (FState s : fs) {
            if (s.alive) {
                queue.add(s);
            }
        }
        Collections.shuffle(queue, rng);
        queue.sort((a, b) -> Integer.compare(speedOf(b), speedOf(a)));
        qIdx = 0;
        processQueue();
    }

    private void rebuildQueuePanel() {
        queuePanel.removeAll();
        for (int i = qIdx; i < queue.size(); i++) {
            Object o = queue.get(i);
            String txt;
            if (o instanceof HState) {
                HState s = (HState) o;
                txt = (i == qIdx ? "\u25B6 " : "    ") + s.hero.getTemplate().getSymbol()
                        + " " + shortName(s.hero.getTemplate().getName());
            } else {
                FState s = (FState) o;
                txt = (i == qIdx ? "\u25B6 " : "    ") + foeShort(s);
            }
            JLabel l = new JLabel(txt);
            l.setForeground(i == qIdx ? Color.YELLOW : Color.GRAY);
            l.setFont(new Font("SansSerif", Font.BOLD, 13));
            l.setAlignmentX(Component.LEFT_ALIGNMENT);
            queuePanel.add(l);
        }
        queuePanel.revalidate();
        queuePanel.repaint();
    }

    private String shortName(String name) {
        int dash = name.indexOf(" - ");
        String base = dash >= 0 ? name.substring(dash + 3) : name;
        return base.length() > 10 ? base.substring(0, 10) : base;
    }

    private void processQueue() {
        while (qIdx < queue.size() && !isAlive(queue.get(qIdx))) {
            qIdx++;
        }
        rebuildQueuePanel();
        if (allFoesDown()) {
            finish(Result.WIN);
            return;
        }
        if (allHeroesDown()) {
            setMessage("Druzyna pokonana...");
            finish(Result.LOSE);
            return;
        }
        if (qIdx >= queue.size()) {
            newRound();
            return;
        }
        Object o = queue.get(qIdx);
        if (o instanceof HState) {
            promptCur((HState) o);
        } else {
            foeAction((FState) o);
        }
    }

    private boolean isAlive(Object o) {
        if (o instanceof HState) {
            return ((HState) o).alive;
        }
        return ((FState) o).alive;
    }

    private void advance() {
        clearCursor();
        qIdx++;
        refreshBars();
        processQueue();
    }

    private void promptCur(HState s) {
        curH = s;
        mode = Mode.CMD;
        showMenu("MAIN");
        clearCursor();
        GameLog.debug("bitwa: tura " + s.hero.getTemplate().name());
        setMessage("Co zrobi <b>" + s.hero.getTemplate().getSymbol() + " "
                + s.hero.getTemplate().getName() + "</b>?<br>Strzalki + Spacja: Atak / Magia / Obrona / Leczenie / Ucieczka");
        rebuildQueuePanel();
        if (!cmdButtons.isEmpty()) {
            SwingUtilities.invokeLater(() -> cmdButtons.get(0).requestFocusInWindow());
        }
    }

    private void promptCur() {
        if (curH != null && curH.alive) {
            promptCur(curH);
        }
    }

    private int effDef(HState s) {
        return s.hero.defense() + (s.shieldRounds > 0 ? s.shieldDef : 0);
    }

    private int effDodge(HState s) {
        return s.hero.dodge() + (s.dodgeRounds > 0 ? s.dodgeBuff : 0);
    }

    private boolean tryDodge(int atkSpd, int defDodge, int defSpd) {
        int chance = defDodge + (defSpd - atkSpd) * 3;
        chance = Math.max(5, Math.min(45, chance));
        return rng.nextInt(100) < chance;
    }

    private void doAttack() {
        if (mode != Mode.CMD) {
            return;
        }
        pendingSpell = null;
        mode = Mode.T_FOE;
        foeCursor = firstAliveFoe();
        paintCursor();
        focusPanel();
        setMessage("Cel ataku: strzalki + Spacja (cel: <b>" + foeName(foeCursor) + "</b>)");
    }

    private int firstAliveFoe() {
        for (int i = 0; i < fs.size(); i++) {
            if (fs.get(i).alive) {
                return i;
            }
        }
        return 0;
    }

    private int firstAliveAlly() {
        for (int i = 0; i < hs.size(); i++) {
            if (hs.get(i).alive) {
                return i;
            }
        }
        return 0;
    }

    private String foeName(int idx) {
        if (idx >= 0 && idx < fs.size()) {
            return fs.get(idx).foe.getName();
        }
        return "?";
    }

    private void doMagic() {
        if (mode != Mode.CMD) {
            return;
        }
        mode = Mode.SPELL;
        GameLog.debug("bitwa: menu czarow, liczba=" + curH.hero.getSpells().size());
        spellMenu.removeAll();
        spellMenu.setLayout(new BoxLayout(spellMenu, BoxLayout.Y_AXIS));
        spellButtons.clear();
        List<String> ids = curH.hero.getSpells();
        for (int i = 0; i < ids.size(); i++) {
            Spell sp = Spell.byId(ids.get(i));
            if (sp == null) {
                continue;
            }
            final int idx = i;
            JButton b = new JButton("\u2022 " + sp.shortDesc());
            b.setFont(new Font("SansSerif", Font.PLAIN, 12));
            b.setEnabled(curH.mana >= sp.cost);
            Keys.noEnter(b);
            hlOnFocus(b);
            b.addActionListener(e -> pickSpell(idx));
            spellMenu.add(b);
            spellButtons.add(b);
        }
        JButton back = new JButton("Wroc");
        Keys.noEnter(back);
        hlOnFocus(back);
        back.addActionListener(e -> promptCur());
        spellMenu.add(back);
        spellButtons.add(back);
        for (JButton sb : spellButtons) {
            markMenuButton(sb, spellButtons);
        }
        showMenu("SPELL");
        setMessage("Wybierz czar dla <b>" + curH.hero.getTemplate().getName() + "</b> (mana "
                + curH.mana + "/" + curH.hero.maxMana() + "): strzalki + Spacja");
        if (!spellButtons.isEmpty()) {
            SwingUtilities.invokeLater(() -> spellButtons.get(0).requestFocusInWindow());
        }
    }

    private void pickSpell(int idx) {
        if (mode != Mode.SPELL) {
            return;
        }
        List<String> ids = curH.hero.getSpells();
        if (idx < 0 || idx >= ids.size()) {
            return;
        }
        Spell sp = Spell.byId(ids.get(idx));
        if (sp == null || curH.mana < sp.cost) {
            return;
        }
        pendingSpell = sp;
        showMenu("MAIN");
        switch (sp.target) {
            case FOE:
                mode = Mode.T_FOE;
                foeCursor = firstAliveFoe();
                paintCursor();
                focusPanel();
                setMessage("<b>" + sp.name + "</b> - cel: strzalki + Spacja");
                break;
            case ALLY:
                mode = Mode.T_ALLY;
                allyCursor = firstAliveAlly();
                paintCursor();
                focusPanel();
                setMessage("<b>" + sp.name + "</b> - komu? strzalki + Spacja");
                break;
            default:
                executeSpell(-1);
                break;
        }
    }

    private void doDefend() {
        if (mode != Mode.CMD) {
            return;
        }
        curH.defend = true;
        setMessage("<b>" + curH.hero.getTemplate().getName() + "</b> przyjmuje postawe obronna!");
        mode = Mode.BUSY;
        Timer t = new Timer(700, e -> advance());
        t.setRepeats(false);
        t.start();
    }

    private void doHeal() {
        if (mode != Mode.CMD) {
            return;
        }
        if (curH.mana < 6) {
            setMessage("Za malo many! (leczenie: 6)");
            return;
        }
        mode = Mode.T_ALLY;
        curHSpellHeal = true;
        allyCursor = firstAliveAlly();
        paintCursor();
        focusPanel();
        setMessage("Kogo uleczyc? Strzalki + Spacja (25+5/lvl, 6 many)");
    }

    private boolean curHSpellHeal = false;

    private void doFlee() {
        if (mode != Mode.CMD) {
            return;
        }
        boolean escape = false;
        for (HState s : hs) {
            if (s.alive && s.hero.getTemplate() == HeroClass.GLUPIEC) {
                escape = true;
                break;
            }
        }
        if (escape || rng.nextBoolean()) {
            setMessage("Uciekliscie!");
            finish(Result.FLEE);
        } else {
            setMessage("Nie udalo sie uciec!");
            mode = Mode.BUSY;
            Timer t = new Timer(700, e -> advance());
            t.setRepeats(false);
            t.start();
        }
    }

    private int heroPower(HState s) {
        return (int) (s.hero.power() * counterMult);
    }

    private void selectFoe(int idx) {
        if (mode != Mode.T_FOE || idx < 0 || idx >= fs.size() || !fs.get(idx).alive) {
            return;
        }
        mode = Mode.BUSY;
        HState s = curH;
        FState t = fs.get(idx);
        if (pendingSpell != null) {
            executeSpellOnFoe(s, pendingSpell, idx);
            return;
        }
        if (tryDodge(s.hero.speed(), t.foe.dodge(), t.foe.getSpd())) {
            final int ti = idx;
            setMessage("<b>" + t.foe.getName() + "</b> robi unik!");
            fxStrike(s.hero.getTemplate().getSymbol(), heroBoxOf(s), foeBoxes.get(ti), Color.YELLOW,
                    "UNIK!", Color.CYAN, this::advance);
            return;
        }
        int base = heroPower(s);
        final int hit = Math.max(1, base + rng.nextInt(base / 2 + 1) - t.foe.getDef() / 2);
        final int ti = idx;
        fxStrike(s.hero.getTemplate().getSymbol(), heroBoxOf(s), foeBoxes.get(ti), Color.YELLOW,
                "-" + hit, Color.RED, () -> {
                    t.hp = Math.max(0, t.hp - hit);
                    if (t.hp <= 0) {
                        t.alive = false;
                        faint(foeSymbols.get(ti));
                    }
                    refreshBars();
                    advance();
                });
    }

    private void executeSpellOnFoe(HState s, Spell sp, int idx) {
        FState t = fs.get(idx);
        s.mana -= sp.cost;
        int base = (int) (heroPower(s) * sp.mult * s.hero.spellMult());
        setMessage("<b>" + s.hero.getTemplate().getName() + "</b> rzuca " + sp.name + "!");
        if (tryDodge(s.hero.speed(), t.foe.dodge(), t.foe.getSpd())) {
            final int ti = idx;
            fxStrike("\u2738", heroBoxOf(s), foeBoxes.get(ti), new Color(150, 200, 255),
                    "UNIK!", Color.CYAN, this::advance);
            return;
        }
        final int hit = Math.max(1, base + rng.nextInt(base / 2 + 1) - t.foe.getDef() / 2);
        final int ti = idx;
        fxStrike("\u2738", heroBoxOf(s), foeBoxes.get(ti), new Color(150, 200, 255),
                "-" + hit, Color.RED, () -> {
                    t.hp = Math.max(0, t.hp - hit);
                    if (t.hp <= 0) {
                        t.alive = false;
                        faint(foeSymbols.get(ti));
                    }
                    if (sp.selfDodge > 0) {
                        s.dodgeBuff = sp.selfDodge;
                        s.dodgeRounds = sp.selfDodgeRounds;
                    }
                    refreshBars();
                    advance();
                });
    }

    private void executeSpell(int allyIdx) {
        HState s = curH;
        Spell sp = pendingSpell;
        s.mana -= sp.cost;
        mode = Mode.BUSY;
        switch (sp.target) {
            case ALLY: {
                HState t = hs.get(allyIdx);
                int amount = sp.healBase + sp.healPerLvl * s.hero.getLevel();
                final int ti = allyIdx;
                setMessage("<b>" + s.hero.getTemplate().getName() + "</b> rzuca " + sp.name + "!");
                fxStrike("\u271A", heroBoxOf(s), heroBoxes.get(ti), Color.GREEN,
                        "+" + amount, Color.GREEN, () -> {
                            t.hp = Math.min(t.hero.maxHp(), t.hp + amount);
                            refreshBars();
                            advance();
                        });
                break;
            }
            case ALL_ALLIES: {
                StringBuilder sb = new StringBuilder("<b>" + s.hero.getTemplate().getName()
                        + "</b> rzuca " + sp.name + "! ");
                for (HState t : hs) {
                    if (!t.alive) {
                        continue;
                    }
                    if (sp.healBase > 0) {
                        t.hp = Math.min(t.hero.maxHp(),
                                t.hp + sp.healBase + sp.healPerLvl * s.hero.getLevel());
                    }
                    if (sp.shieldDef > 0) {
                        t.shieldDef = sp.shieldDef;
                        t.shieldRounds = sp.shieldRounds;
                    }
                }
                if (sp.shieldDef > 0) {
                    sb.append("Tarcza +").append(sp.shieldDef).append(" DEF!");
                } else {
                    sb.append("Druzyna uleczona!");
                }
                setMessage(sb.toString());
                refreshBars();
                Timer tm = new Timer(800, e -> advance());
                tm.setRepeats(false);
                tm.start();
                break;
            }
            default: {
                if (sp.shieldDef > 0) {
                    s.shieldDef = sp.shieldDef;
                    s.shieldRounds = sp.shieldRounds;
                    setMessage("<b>" + s.hero.getTemplate().getName() + "</b>: tarcza +"
                            + sp.shieldDef + " DEF!");
                } else if (sp.dodgePct > 0) {
                    s.dodgeBuff = sp.dodgePct;
                    s.dodgeRounds = sp.dodgeRounds;
                    setMessage("<b>" + s.hero.getTemplate().getName() + "</b>: unik +"
                            + sp.dodgePct + "%!");
                }
                refreshBars();
                Timer tm = new Timer(800, e -> advance());
                tm.setRepeats(false);
                tm.start();
                break;
            }
        }
    }

    private void selectAlly(int idx) {
        if (mode != Mode.T_ALLY || idx < 0 || idx >= hs.size() || !hs.get(idx).alive) {
            return;
        }
        HState s = curH;
        if (curHSpellHeal) {
            curHSpellHeal = false;
            if (s.mana < 6) {
                setMessage("Za malo many! (leczenie: 6)");
                mode = Mode.CMD;
                showMenu("MAIN");
                return;
            }
            s.mana -= 6;
            int amount = 25 + 5 * s.hero.getLevel();
            HState t = hs.get(idx);
            mode = Mode.BUSY;
            final int ti = idx;
            fxStrike("+", heroBoxOf(s), heroBoxes.get(ti), Color.GREEN,
                    "+" + amount, Color.GREEN, () -> {
                        t.hp = Math.min(t.hero.maxHp(), t.hp + amount);
                        refreshBars();
                        advance();
                    });
            return;
        }
        if (pendingSpell != null) {
            executeSpell(idx);
        }
    }

    private JPanel heroBoxOf(HState s) {
        return heroBoxes.get(hs.indexOf(s));
    }

    private void foeAction(FState f) {
        List<Integer> targets = new ArrayList<>();
        for (int i = 0; i < hs.size(); i++) {
            if (hs.get(i).alive) {
                targets.add(i);
            }
        }
        if (targets.isEmpty()) {
            advance();
            return;
        }
        final int ti = targets.get(rng.nextInt(targets.size()));
        HState t = hs.get(ti);
        mode = Mode.BUSY;
        showMenu("MAIN");
        setMessage("<b>" + f.foe.getName() + "</b> atakuje " + t.hero.getTemplate().getName() + "!");
        Timer pause = new Timer(450, e -> {
            if (tryDodge(f.foe.getSpd(), effDodge(t), t.hero.speed())) {
                fxStrike(foeSymbol(f.foe), foeBoxOf(f), heroBoxes.get(ti), Color.ORANGE,
                        "UNIK!", Color.CYAN, this::advance);
                return;
            }
            int e2 = f.foe.getAtk() + rng.nextInt(f.foe.getAtk() / 2 + 1);
            int dmg = Math.max(1, e2 - effDef(t) / 2);
            if (t.defend) {
                dmg = Math.max(1, dmg / 2);
            }
            final int hit = dmg;
            fxStrike(foeSymbol(f.foe), foeBoxOf(f), heroBoxes.get(ti), Color.ORANGE,
                    "-" + hit, Color.RED, () -> {
                        t.hp = Math.max(0, t.hp - hit);
                        if (t.hp <= 0) {
                            t.alive = false;
                            faint(heroSymbols.get(ti));
                        }
                        refreshBars();
                        advance();
                    });
        });
        pause.setRepeats(false);
        pause.start();
    }

    private JPanel foeBoxOf(FState f) {
        return foeBoxes.get(fs.indexOf(f));
    }

    private boolean allFoesDown() {
        for (FState s : fs) {
            if (s.alive) {
                return false;
            }
        }
        return true;
    }

    private boolean allHeroesDown() {
        for (HState s : hs) {
            if (s.alive) {
                return false;
            }
        }
        return true;
    }

    /** Esc jako wstecz: z wyboru czaru/celu do menu akcji. */
    public void handleEsc() {
        if (!isShowing()) {
            return;
        }
        if (mode == Mode.SPELL || mode == Mode.T_FOE || mode == Mode.T_ALLY) {
            pendingSpell = null;
            promptCur();
        }
    }

    private void finish(Result r) {
        if (mode == Mode.DONE) {
            return;
        }
        GameLog.debug("bitwa: wynik " + r);
        mode = Mode.DONE;
        for (HState s : hs) {
            s.hero.setHpCur(s.hp);
        }
        cb.accept(r);
    }

    private void refreshBars() {
        for (int i = 0; i < fs.size(); i++) {
            FState s = fs.get(i);
            JProgressBar bar = foeBars.get(i);
            int fmax = Math.max(1, s.maxHp);
            int fcur = Math.max(0, Math.min(s.hp, fmax));
            bar.setMaximum(fmax);
            bar.setValue(fcur);
            bar.setString(fcur + "/" + fmax);
        }
        for (int i = 0; i < hs.size(); i++) {
            HState s = hs.get(i);
            JProgressBar bar = heroBars.get(i);
            int mhp = Math.max(1, s.hero.maxHp());
            int chp = Math.max(0, Math.min(s.hp, mhp));
            bar.setMaximum(mhp);
            bar.setValue(chp);
            bar.setString(chp + "/" + mhp);
            heroMana.get(i).setText("Mana: " + s.mana + "/" + s.hero.maxMana());
        }
    }

    private Point centerInOverlay(JComponent comp) {
        return SwingUtilities.convertPoint(comp, comp.getWidth() / 2, comp.getHeight() / 2, fxOverlay);
    }

    /** Animacja: symbol leci do celu, blysk, liczba obrazen. Wszystko w tym samym oknie. */
    private void fxStrike(String flyTxt, JComponent from, JComponent targetBox, Color color,
                          String dmgTxt, Color dmgColor, Runnable done) {
        Point a = centerInOverlay(from);
        Point b = centerInOverlay(targetBox);
        JLabel fly = new JLabel(flyTxt);
        fly.setFont(new Font("SansSerif", Font.BOLD, 24));
        fly.setForeground(color);
        fly.setSize(fly.getPreferredSize());
        fly.setLocation(a.x, a.y);
        fxOverlay.add(fly);
        fxOverlay.repaint();
        final int steps = 12;
        final int[] s = {0};
        Timer move = new Timer(30, null);
        move.addActionListener(e -> {
            s[0]++;
            float k = Math.min(1f, s[0] / (float) steps);
            fly.setLocation((int) (a.x + (b.x - a.x) * k), (int) (a.y + (b.y - a.y) * k));
            if (s[0] >= steps) {
                move.stop();
                fxOverlay.remove(fly);
                flashAndNumber(targetBox, dmgTxt, dmgColor, done);
            }
        });
        move.start();
    }

    private void flashAndNumber(JComponent box, String dmgTxt, Color dmgColor, Runnable done) {
        Color orig = box.getBackground();
        JLabel num = new JLabel(dmgTxt);
        num.setFont(new Font("SansSerif", Font.BOLD, 18));
        num.setForeground(dmgColor);
        num.setSize(num.getPreferredSize());
        Point c = centerInOverlay(box);
        num.setLocation(c.x, c.y - 10);
        fxOverlay.add(num);
        fxOverlay.repaint();
        final int[] s = {0};
        Timer t = new Timer(90, null);
        t.addActionListener(e -> {
            s[0]++;
            box.setBackground(s[0] % 2 == 1 ? Color.WHITE : orig);
            num.setLocation(num.getX(), num.getY() - 6);
            if (s[0] >= 6) {
                t.stop();
                box.setBackground(orig);
                fxOverlay.remove(num);
                fxOverlay.repaint();
                done.run();
            }
        });
        t.start();
    }

    private void setMessage(String html) {
        message.setText(html.startsWith("<html>") ? html : "<html>" + html + "</html>");
    }

    private void showMenu(String name) {
        ((CardLayout) menu.getLayout()).show(menu, name);
    }
}
