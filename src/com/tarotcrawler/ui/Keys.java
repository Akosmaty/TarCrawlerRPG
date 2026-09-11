package com.tarotcrawler.ui;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/** Pomocnik klawiatury: gra uzywa TYLKO strzalek + spacji.
 *  Enter jest wylaczany na przyciskach (spacja dziala natywnie na focusie). */
public final class Keys {

    private Keys() {}

    /** Wylacza aktywacje Enterem - zostaje tylko Spacja. */
    public static void noEnter(AbstractButton b) {
        b.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ENTER"), "none");
    }

    /** Przechodzenie focusem po liscie (pomija wylaczone). Strzalki + spacja. */
    public static void cycle(List<? extends JComponent> items, int dir) {
        List<JComponent> en = new ArrayList<>();
        for (JComponent c : items) {
            if (c.isEnabled() && c.isVisible() && c.isFocusable()) {
                en.add(c);
            }
        }
        if (en.isEmpty()) {
            return;
        }
        int cur = -1;
        for (int i = 0; i < en.size(); i++) {
            if (en.get(i).isFocusOwner()) {
                cur = i;
                break;
            }
        }
        int next = cur < 0 ? (dir > 0 ? 0 : en.size() - 1)
                : (cur + dir + en.size()) % en.size();
        en.get(next).requestFocusInWindow();
    }
}
