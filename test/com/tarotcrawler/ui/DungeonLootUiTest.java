package com.tarotcrawler.ui;

import com.tarotcrawler.TestRunner;
import com.tarotcrawler.model.Dungeon;
import com.tarotcrawler.model.Enemy;
import com.tarotcrawler.model.EnemyTable;
import com.tarotcrawler.model.Hero;
import com.tarotcrawler.model.HeroClass;
import com.tarotcrawler.model.LootTable;
import com.tarotcrawler.model.RunState;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/** Przepływ lootu bez okna: WIN -> 4 propozycje widoczne -> wybor -> komu -> sprzet. */
public class DungeonLootUiTest {

    private DungeonPanel panel;
    private final List<Hero> party =
            Arrays.asList(new Hero(HeroClass.GLUPIEC), new Hero(HeroClass.MAG));

    private Object field(String name) throws Exception {
        Field f = DungeonPanel.class.getDeclaredField(name);
        f.setAccessible(true);
        return f.get(panel);
    }

    private void setField(String name, Object val) throws Exception {
        Field f = DungeonPanel.class.getDeclaredField(name);
        f.setAccessible(true);
        f.set(panel, val);
    }

    private Object call(String name, Class<?>[] types, Object[] args) throws Exception {
        Method m = DungeonPanel.class.getDeclaredMethod(name, types);
        m.setAccessible(true);
        final Object[] out = new Object[1];
        final Exception[] err = new Exception[1];
        SwingUtilities.invokeAndWait(() -> {
            try {
                out[0] = m.invoke(panel, args);
            } catch (Exception e) {
                err[0] = e;
            }
        });
        if (err[0] != null) {
            throw err[0];
        }
        return out[0];
    }

    public void testLootOverlayPokazujeWszystkie() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            panel = new DungeonPanel();
            panel.startRun(party, Dungeon.MIECZE);
        });
        Random r = new Random(31);
        Enemy foe = EnemyTable.pipFor(Dungeon.MIECZE, 3, r);
        LootTable.Item[] drops = LootTable.roll(foe, Dungeon.MIECZE, r);
        setField("pendingDrops", drops);
        setField("mode", Enum.valueOf((Class<Enum>) Class.forName("com.tarotcrawler.ui.DungeonPanel$Mode"), "LOOT"));
        call("showLoot", new Class<?>[0], new Object[0]);

        SwingUtilities.invokeAndWait(() -> {
            try {
                JPanel overlay = (JPanel) field("lootOverlay");
                TestRunner.ok(overlay.isVisible(), "overlay widoczny");
                JButton[] btns = (JButton[]) field("lootButtons");
                TestRunner.eq(4, btns.length, "3 loot + pomin");
                overlay.doLayout();
                for (int i = 0; i < btns.length; i++) {
                    java.awt.Dimension d = btns[i].getPreferredSize();
                    TestRunner.ok(d.width > 0 && d.height > 0, "przycisk " + i + " ma rozmiar");
                    TestRunner.ok(btns[i].isVisible(), "przycisk " + i + " widoczny");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void testWyborLootuIDalej() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            panel = new DungeonPanel();
            panel.startRun(party, Dungeon.MIECZE);
        });
        Random r = new Random(32);
        Enemy foe = EnemyTable.pipFor(Dungeon.MIECZE, 3, r);
        setField("pendingDrops", LootTable.roll(foe, Dungeon.MIECZE, r));
        setField("mode", Enum.valueOf((Class<Enum>) Class.forName("com.tarotcrawler.ui.DungeonPanel$Mode"), "LOOT"));
        call("showLoot", new Class<?>[0], new Object[0]);
        call("pickLoot", new Class<?>[]{int.class}, new Object[]{0});
        TestRunner.eq("HERO", String.valueOf(field("mode")), "po loot wybór kompana");

        call("pickHero", new Class<?>[]{int.class}, new Object[]{1});
        TestRunner.eq("EXPLORE", String.valueOf(field("mode")), "po kom্পanie eksploracja");
        TestRunner.ok(!((JPanel) field("lootOverlay")).isVisible(), "overlay schowany");
    }
}
