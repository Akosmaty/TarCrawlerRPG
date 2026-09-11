package com.tarotcrawler.ui;

import com.tarotcrawler.TestRunner;
import com.tarotcrawler.model.Dungeon;
import com.tarotcrawler.model.Enemy;
import com.tarotcrawler.model.EnemyTable;
import com.tarotcrawler.model.Hero;
import com.tarotcrawler.model.HeroClass;
import com.tarotcrawler.model.RunState;

import javax.swing.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/** REGRESJA: druga bitwa (ranni bohaterowie) musi wystartowac bez wyjatku,
 *  a paski HP miec poprawne zakresy. */
public class BattleStartTest {

    private Object field(Object o, String name) throws Exception {
        Field f = o.getClass().getDeclaredField(name);
        f.setAccessible(true);
        return f.get(o);
    }

    public void testStartBitwyZRannymi() throws Exception {
        List<Hero> party = Arrays.asList(new Hero(HeroClass.GLUPIEC), new Hero(HeroClass.MAG));
        for (Hero h : party) {
            h.setHpCur(h.maxHp() / 3);
        }
        RunState run = new RunState(party, Dungeon.MIECZE);
        Random r = new Random(41);
        List<Enemy> foes = new ArrayList<>();
        foes.add(EnemyTable.scale(EnemyTable.forFloor(Dungeon.MIECZE, 2, r), 2));

        BattlePanel bp = new BattlePanel();
        final Exception[] err = new Exception[1];
        SwingUtilities.invokeAndWait(() -> {
            try {
                bp.startBattle(run, foes, false, x -> {});
            } catch (Exception e) {
                err[0] = e;
            }
        });
        TestRunner.ok(err[0] == null, "start bitwy nie rzuca: " + err[0]);

        List<JProgressBar> heroBars = (List<JProgressBar>) field(bp, "heroBars");
        TestRunner.eq(2, heroBars.size(), "2 paski");
        for (JProgressBar bar : heroBars) {
            TestRunner.ok(bar.getMaximum() >= 1, "max >= 1");
            TestRunner.ok(bar.getValue() >= bar.getMinimum()
                    && bar.getValue() <= bar.getMaximum(), "wartosc w zakresie");
        }
        List<JProgressBar> foeBars = (List<JProgressBar>) field(bp, "foeBars");
        TestRunner.eq(1, foeBars.size(), "1 pasek wroga");
    }

    public void testStartBitwyFullHp() throws Exception {
        List<Hero> party = Arrays.asList(new Hero(HeroClass.CESARZ));
        RunState run = new RunState(party, Dungeon.BULAWY);
        Random r = new Random(42);
        final BattlePanel[] bp = new BattlePanel[1];
        final Exception[] err = new Exception[1];
        SwingUtilities.invokeAndWait(() -> {
            try {
                bp[0] = new BattlePanel();
                bp[0].startBattle(run,
                        java.util.Collections.singletonList(EnemyTable.randomBoss(r)),
                        true, x -> {});
            } catch (Exception e) {
                err[0] = e;
            }
        });
        TestRunner.ok(err[0] == null, "start bossa nie rzuca: " + err[0]);
    }
}
