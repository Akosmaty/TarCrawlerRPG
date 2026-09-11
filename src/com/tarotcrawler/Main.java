package com.tarotcrawler;

import com.tarotcrawler.ui.MainFrame;
import com.tarotcrawler.util.GameLog;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        GameLog.init();
        SwingUtilities.invokeLater(() -> {
            try {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
                GameLog.info("okno glowne widoczne");
            } catch (Throwable t) {
                GameLog.error("start nieudany", t);
                throw t;
            }
        });
    }
}
