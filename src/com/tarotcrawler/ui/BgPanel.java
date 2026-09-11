package com.tarotcrawler.ui;

import com.tarotcrawler.util.GameLog;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * JPanel z tlem (obrazek) jako podklad.
 */
public class BgPanel extends JPanel {
    private BufferedImage bgImg;

    public BgPanel(LayoutManager layout) {
        super(layout);
        setBackground(Color.BLACK);
        setOpaque(true);
    }

    public void setBg(BufferedImage img) {
        this.bgImg = img;
        repaint();
    }

    public void setBg(String path) {
        try {
            this.bgImg = ImageIO.read(new File(path));
            GameLog.info("BgPanel: wczytano " + path + " (" + bgImg.getWidth() + "x" + bgImg.getHeight() + ")");
        } catch (Exception e) {
            GameLog.warn("BgPanel: nie mozna wczytac " + path + ": " + e.getMessage());
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        // 1. Tlo kolor
        g2.setColor(getBackground());
        g2.fillRect(0, 0, getWidth(), getHeight());
        // 2. Tlo obrazek
        if (bgImg != null) {
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(bgImg, 0, 0, getWidth(), getHeight(), null);
        }
        // 3. Dzieci (przyciski, text itp.)
        paintChildren(g);
    }
}
