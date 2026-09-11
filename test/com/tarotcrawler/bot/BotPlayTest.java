package com.tarotcrawler.bot;

import com.tarotcrawler.TestRunner;
import com.tarotcrawler.model.Dungeon;
import com.tarotcrawler.model.Hero;
import com.tarotcrawler.model.HeroClass;
import com.tarotcrawler.model.RunState;
import com.tarotcrawler.ui.BattlePanel;
import com.tarotcrawler.ui.DungeonPanel;
import com.tarotcrawler.util.GameLog;

import javax.swing.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/** BOT - test manualny: gra sam jak gracz (mapa, bitwa, loot, awanse).
 *  Uruchomienie: run_bot.bat. Wszystko loguje przez GameLog. */
public class BotPlayTest {

    private DungeonPanel dungeon;
    private BattlePanel battle;

    private Object field(Object o, String name) throws Exception {
        Field f = o.getClass().getDeclaredField(name);
        f.setAccessible(true);
        return f.get(o);
    }

    private Object eden(Runnable r) throws Exception {
        final Exception[] err = new Exception[1];
        SwingUtilities.invokeAndWait(() -> {
            try {
                r.run();
            } catch (Exception e) {
                err[0] = e;
            }
        });
        if (err[0] != null) {
            throw err[0];
        }
        return null;
    }

    private Object call(Object o, String name, Class<?>[] types, Object[] args) throws Exception {
        Method m = o.getClass().getDeclaredMethod(name, types);
        m.setAccessible(true);
        final Object[] out = new Object[1];
        final Exception[] err = new Exception[1];
        SwingUtilities.invokeAndWait(() -> {
            try {
                out[0] = m.invoke(o, args);
            } catch (Exception e) {
                err[0] = e;
            }
        });
        if (err[0] != null) {
            throw err[0];
        }
        return out[0];
    }

    private String dmode() throws Exception {
        final String[] s = new String[1];
        SwingUtilities.invokeAndWait(() -> {
            try {
                s[0] = String.valueOf(field(dungeon, "mode"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return s[0];
    }

    private String bmode() throws Exception {
        final String[] s = new String[1];
        SwingUtilities.invokeAndWait(() -> {
            try {
                s[0] = String.valueOf(field(battle, "mode"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return s[0];
    }

    public void testBotGra() throws Exception {
        GameLog.init();
        GameLog.info("BOT: start testu manualnego");
        SwingUtilities.invokeAndWait(() -> {
            dungeon = new DungeonPanel();
            battle = new BattlePanel();
        });
        List<Hero> party = Arrays.asList(new Hero(HeroClass.GLUPIEC), new Hero(HeroClass.MAG));
        SwingUtilities.invokeAndWait(() -> {
            dungeon.setBattleStarter((run, foes, isBoss, cb) -> {
                GameLog.info("BOT: bitwa start, wrogow=" + foes.size());
                battle.startBattle(run, foes, isBoss, cb);
            });
            dungeon.startRun(party, Dungeon.MIECZE);
        });
        GameLog.info("BOT: loch MIECZE, pietro 1");

        int[][] seq = new int[200][2];
        int q = 0;
        for (int i = 0; i < 9 && q < 199; i++) {
            seq[q][0] = 0;
            seq[q++][1] = -1;
        }
        boolean right = true;
        for (int row = 0; row < 10 && q < 190; row++) {
            for (int i = 0; i < 9 && q < 199; i++) {
                seq[q][0] = right ? 1 : -1;
                seq[q++][1] = 0;
            }
            if (row < 9 && q < 199) {
                seq[q][0] = 0;
                seq[q++][1] = 1;
            }
            right = !right;
        }
        boolean walkaByla = false;
        for (int step = 0; step < q; step++) {
            final int dx = seq[step][0];
            final int dy = seq[step][1];
            SwingUtilities.invokeAndWait(() -> dungeon.handleArrows(dx, dy));
            Thread.sleep(120);
            if (!dmode().equals("EXPLORE")) {
                walkaByla = true;
                break;
            }
        }
        TestRunner.ok(walkaByla, "bot wszedl do walki");
        GameLog.info("BOT: w bitwie, gram...");
        Thread.sleep(1500);

        for (int round = 0; round < 40; round++) {
            if (!dmode().equals("BATTLE")) {
                break;
            }
            if (bmode().equals("CMD")) {
                call(battle, "doAttack", new Class<?>[0], new Object[0]);
            } else if (bmode().equals("T_FOE")) {
                int idx = pierwszyZywy();
                if (idx >= 0) {
                    call(battle, "selectFoe", new Class<?>[]{int.class}, new Object[]{idx});
                }
            }
            Thread.sleep(1300);
        }
        String po = dmode();
        GameLog.info("BOT: po bitwie tryb=" + po);
        TestRunner.ok(!po.equals("BATTLE"), "bitwa sie zakonczyla: " + po);

        if (po.equals("LOOT")) {
            call(dungeon, "pickLoot", new Class<?>[]{int.class}, new Object[]{0});
            Thread.sleep(300);
            if (dmode().equals("HERO")) {
                call(dungeon, "pickHero", new Class<?>[]{int.class}, new Object[]{0});
                Thread.sleep(300);
            }
            int guard = 0;
            while (dmode().equals("LEVEL") && guard++ < 10) {
                call(dungeon, "nextLevelUp", new Class<?>[0], new Object[0]);
                Thread.sleep(200);
            }
            TestRunner.eq("EXPLORE", dmode(), "po loocie eksploracja");
        }

        int expRazem = 0;
        for (Hero h : party) {
            expRazem += h.getExp() + (h.getLevel() - 1) * 1000;
        }
        GameLog.info("BOT: exp razem=" + expRazem + " lvl0=" + party.get(0).getLevel()
                + " lvl1=" + party.get(1).getLevel());
        TestRunner.ok(expRazem > 0 || !po.equals("EXPLORE"), "postacie cos ugraly albo padly z honorem");
        GameLog.info("BOT: koniec testu manualnego");
    }

    private int pierwszyZywy() throws Exception {
        final int[] idx = {-1};
        SwingUtilities.invokeAndWait(() -> {
            try {
                java.util.List foes = (java.util.List) field(battle, "fs");
                for (int i = 0; i < foes.size(); i++) {
                    Object st = foes.get(i);
                    Field al = st.getClass().getDeclaredField("alive");
                    al.setAccessible(true);
                    if (al.getBoolean(st)) {
                        idx[0] = i;
                        break;
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return idx[0];
    }
}
