package com.tarotcrawler.ui;

import com.tarotcrawler.model.Gear;
import com.tarotcrawler.model.Hero;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** Widok ekwipunku druzyny: 6 slotow na postac, Spacja na zajetym slocie zdejmuje (niszczy). */
public class EquipPanel extends JPanel {

    private final JLabel info = new JLabel(" ", SwingConstants.CENTER);
    private final JPanel cols = new JPanel();
    private final JButton backBtn = new JButton("Wroc");
    private final List<JComponent> items = new ArrayList<>();
    private List<Hero> party = new ArrayList<>();
    private Runnable onBack = () -> {};

    public EquipPanel() {
        super(new BorderLayout());
        setBackground(new Color(20, 20, 25));
        JLabel title = new JLabel("EKWIPUNEK DRUZYNY - strzalki + Spacja", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 6, 0));
        add(title, BorderLayout.NORTH);
        cols.setBackground(new Color(20, 20, 25));
        add(cols, BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout());
        south.setBackground(new Color(20, 20, 25));
        info.setForeground(new Color(255, 230, 150));
        info.setFont(new Font("SansSerif", Font.BOLD, 14));
        south.add(info, BorderLayout.NORTH);
        JPanel bottom = new JPanel();
        bottom.setBackground(new Color(20, 20, 25));
        Keys.noEnter(backBtn);
        backBtn.addActionListener(e -> onBack.run());
        bottom.add(backBtn);
        south.add(bottom, BorderLayout.SOUTH);
        add(south, BorderLayout.SOUTH);
    }

    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
    }

    public void showParty(List<Hero> party) {
        this.party = new ArrayList<>(party);
        info.setText("Spacja na zajetym slocie zdejmuje przedmiot (przepada).");
        rebuild();
    }

    private void rebuild() {
        cols.removeAll();
        items.clear();
        cols.setLayout(new GridLayout(1, Math.max(1, party.size()), 10, 0));
        cols.setBorder(BorderFactory.createEmptyBorder(4, 16, 4, 16));
        for (Hero h : party) {
            JPanel col = new JPanel();
            col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
            col.setBackground(new Color(20, 20, 25));
            JLabel head = new JLabel(h.getTemplate().getSymbol() + " " + h.getTemplate().getName()
                    + " lvl " + h.getLevel(), SwingConstants.CENTER);
            head.setForeground(Color.YELLOW);
            head.setAlignmentX(Component.CENTER_ALIGNMENT);
            col.add(head);
            for (Gear.Slot slot : Gear.Slot.values()) {
                Gear g = h.getGear().get(slot);
                JButton b = new JButton(slotLabel(slot) + ": " + (g == null ? "--" : g.describe()));
                b.setFont(new Font("SansSerif", Font.PLAIN, 11));
                b.setEnabled(g != null);
                Keys.noEnter(b);
                if (g != null) {
                    b.addActionListener(e -> {
                        h.unequip(slot);
                        info.setText("Zdjeto i porzucono: " + g.describe());
                        rebuild();
                    });
                }
                b.setAlignmentX(Component.CENTER_ALIGNMENT);
                b.setMaximumSize(new Dimension(340, 36));
                col.add(b);
                items.add(b);
            }
            cols.add(col);
        }
        items.add(backBtn);
        revalidate();
        repaint();
        focusFirst();
    }

    private static String slotLabel(Gear.Slot s) {
        if (s == Gear.Slot.HAND_L || s == Gear.Slot.HAND_R) {
            return "Reka";
        }
        return s.getLabel();
    }

    public java.util.List<JComponent> getItems() {
        return items;
    }

    public void focusFirst() {
        SwingUtilities.invokeLater(() -> {
            for (JComponent c : items) {
                if (c.isEnabled() && c.isVisible()) {
                    c.requestFocusInWindow();
                    break;
                }
            }
        });
    }
}
