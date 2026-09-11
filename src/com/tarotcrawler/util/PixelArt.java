package com.tarotcrawler.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Wszystkie grafiki gry jako dokladne definicje pikseli (kod).
 * Kazdy znak w szablonie = jeden kolor z palety.
 * Piksel jest powielany do rozmiaru docelowego (pixel-art scaling).
 */
public final class PixelArt {

    private static final Map<Character, int[]> PAL = new HashMap<>();
    static {
        pal('_', 0, 0, 0, 0);
        pal('k', 15, 12, 20);
        pal('K', 30, 25, 40);
        pal('g', 180, 150, 40);
        pal('G', 210, 180, 50);
        pal('w', 160, 160, 175);
        pal('W', 190, 190, 200);
        pal('s', 130, 130, 145);
        pal('S', 100, 100, 120);
        pal('r', 170, 40, 35);
        pal('R', 120, 25, 20);
        pal('b', 60, 100, 170);
        pal('B', 40, 65, 130);
        pal('n', 140, 100, 50);
        pal('N', 95, 70, 35);
        pal('z', 50, 140, 85);
        pal('Z', 35, 95, 60);
        pal('p', 170, 65, 140);
        pal('P', 100, 50, 150);
        pal('o', 190, 120, 40);
        pal('O', 150, 85, 25);
        pal('c', 85, 85, 105);
        pal('C', 60, 60, 80);
        pal('y', 210, 210, 120);
        pal('t', 155, 50, 50);
        pal('x', 190, 155, 120);
        pal('X', 155, 120, 85);
        pal('h', 120, 85, 50);
        pal('H', 70, 50, 35);
        pal('d', 35, 35, 45);
        pal('D', 18, 13, 22);
        pal('e', 85, 50, 155);
        pal('E', 135, 85, 190);
    }

    private static void pal(char c, int r, int g, int b) {
        PAL.put(c, new int[]{r, g, b, 255});
    }
    private static void pal(char c, int r, int g, int b, int a) {
        PAL.put(c, new int[]{r, g, b, a});
    }

    // ==================== IKONY 32×32 ====================

    static String[] ICON_HELM = {
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "___________gGGgggG_______________",
        "__________gGGGGGGGg______________",
        "_________gGGGGGGGGGg_____________",
        "________gGGGGGGGGGGGg____________",
        "________gGGGgggGGGGGg____________",
        "_______gGGg_____gGGGg____________",
        "_______gGg_______gGGg____________",
        "_______gGg_______gGGg____________",
        "_______gGGg_____gGGGg____________",
        "_______gGGGGgggGGGGGg____________",
        "_______gGGGGGGGGGGGGg____________",
        "_______sGGGGGGGGGGGs_____________",
        "_______sSSSSSSSSSSSSs____________",
        "______sSSSSSSSSSSSSSs____________",
        "______sSSSSSSSSSSSSSs____________",
        "_____sSSSSSSSSSSSSSSSs___________",
        "_____sSSSSSSSSSSSSSSSs___________",
        "____sSSSSSSSSSSSSSSSSSs__________",
        "____sSSSSSSSSSSSSSSSSSs__________",
        "___sSSSSSSSSSSSSSSSSSSSs_________",
        "___sSSSSSSSSSSSSSSSSSSSs_________",
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
    };

    static String[] ICON_ARMOR = {
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "_________sSSSSSSSSs______________",
        "________sSSSSSSSSSSs_____________",
        "_______sSSSSSSSSSSSSs____________",
        "_______sSSwSSSSSwwSSs____________",
        "______sSSSSSSSSSSSSSSs___________",
        "______sSwSSSSSSSSSwwSs___________",
        "_____sSSSSSSSSSSSSSSSSs__________",
        "_____sSSSSSSSSSSSSSSSSs__________",
        "_____sSSSSSSgggSSSSSSSs__________",
        "_____sSSSSSgGGgSSSSSSSs__________",
        "_____sSSSSSgGGgSSSSSSSs__________",
        "_____sSSSSSSgggSSSSSSSs__________",
        "_____sSSSSSSSSSSSSSSSSs__________",
        "_____sSSSSSSSSSSSSSSSSs__________",
        "_____sSSSSSSSSSSSSSSSSs__________",
        "_____sSSSSSSSSSSSSSSSSs__________",
        "______sSSSSSSSSSSSSSSs___________",
        "______sSSSSSSSSSSSSSSs___________",
        "_______sSSSSSSSSSSSSs____________",
        "_______sSSSSSSSSSSSs_____________",
        "________sSSSSSSSSSs______________",
        "________sSSSSSSSSSs______________",
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
    };

    static String[] ICON_BOOTS = {
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "___________nnn______nnn__________",
        "__________nnNnn____nnNnn_________",
        "_________nnNNnn___nnNNnn_________",
        "_________nnNNnn___nnNNnn_________",
        "_________nnNNnn___nnNNnn_________",
        "_________nnNNnn___nnNNnn_________",
        "_________nnNNnn___nnNNnn_________",
        "_________nnNNnn___nnNNnn_________",
        "_________nnNNnn___nnNNnn_________",
        "________nnNNNnn_nnNNNnn__________",
        "________nnNNNnn_nnNNNnn__________",
        "_______nnNNNNnnnNNNNnn___________",
        "_______nNNNNNNnNNNNNnn___________",
        "_______NNNNNNNNNNNNNNn___________",
        "_______NNNNNNNNNNNNNNN___________",
        "________________________________",
        "________________________________",
        "________________________________",
    };

    static String[] ICON_SWORD = {
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "_________________ww______________",
        "________________wSw______________",
        "_______________wSw_______________",
        "______________wSw________________",
        "_____________wSw_________________",
        "____________wSw__________________",
        "___________wSw___________________",
        "__________wSw____________________",
        "_________wSw_____________________",
        "________wSw______________________",
        "_______wSw_______________________",
        "______wSw________________________",
        "_____wSw_________________________",
        "____wSw__________________________",
        "___wSw___________________________",
        "___gGg___________________________",
        "__gGGGg__________________________",
        "__gGGGg__________________________",
        "___gGg___________________________",
        "____gn___________________________",
        "____gn___________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
    };

    static String[] ICON_SHIELD = {
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "___________gGGGg_________________",
        "__________gGGGGGg________________",
        "_________gGGGGGGGg_______________",
        "________gGGgGGgGGGg______________",
        "________gGg_gGg_gGg______________",
        "________gGg_gGg_gGg______________",
        "________gGGgGGgGGGg______________",
        "_________gGGGGGGGg_______________",
        "_________gGGgggGGGg______________",
        "________gGGg_gGgGGGg_____________",
        "________gGg__gGgGGg______________",
        "________gGg__gGgGGg______________",
        "________gGGggGGgGGGg_____________",
        "_________gGGGGGGGg_______________",
        "__________gGGGGGg________________",
        "___________gGGGg_________________",
        "___________gGGGg_________________",
        "__________gGGGGGg________________",
        "_________gGGGGGGGg_______________",
        "________gGGGGGGGGGg______________",
        "________gGGGGGGGGGg______________",
        "_________gGGGGGGGg_______________",
        "__________gGGGGGg________________",
        "___________gGGg__________________",
        "________________________________",
        "________________________________",
    };

    static String[] ICON_AMULET = {
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "____________gnng_________________",
        "___________g    g________________",
        "__________g      g_______________",
        "_________g   gg   g______________",
        "________g   gGGg   g_____________",
        "________g  gGGGGg  g_____________",
        "________g  gGGGGg  g_____________",
        "________g   gGGg   g_____________",
        "_________g   gg   g______________",
        "__________g      g_______________",
        "___________g    g________________",
        "____________gnng_________________",
        "_____________gn__________________",
        "_____________gn__________________",
        "_____________gn__________________",
        "_____________gn__________________",
        "____________g_g__________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
    };

    static String[] ICON_WAND = {
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "_________________yWWy____________",
        "________________WWWWy____________",
        "________________WWWWy____________",
        "_________________yWWy____________",
        "__________________gy_____________",
        "_________________gn______________",
        "________________gn_______________",
        "_______________gn________________",
        "______________gn_________________",
        "_____________gn__________________",
        "____________gn___________________",
        "___________gn____________________",
        "__________gn_____________________",
        "_________gn______________________",
        "________gn_______________________",
        "_______gn________________________",
        "______gn_________________________",
        "_____gn__________________________",
        "____gn___________________________",
        "___gn____________________________",
        "___Pn____________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
    };

    static String[] ICON_KEY = {
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "__________gGGGg__________________",
        "________gGggggGg_________________",
        "________gG____gG_________________",
        "________gG____gG_________________",
        "________gGggggGg_________________",
        "__________gGGg___________________",
        "___________gn____________________",
        "___________gn____________________",
        "___________gn____________________",
        "___________gn____________________",
        "___________gGGn__________________",
        "___________gn____________________",
        "___________gGGn__________________",
        "___________gn____________________",
        "___________gGGn__________________",
        "___________gn____________________",
        "___________gn____________________",
        "___________gn____________________",
        "___________gn____________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
        "________________________________",
    };

    // ==================== PIPSY 16×16 ====================

    static String[] PIP_KIELICHY = {
        "________________",
        "______KKK_______",
        "_____KwwwwK_____",
        "____KwwwwwwK____",
        "____KwwWWwwK____",
        "___KwwWWWWwwK___",
        "___KwWWWWWWwK___",
        "___KwWWWWWWwK___",
        "___KwWWWWWWwK___",
        "____KwWWWWwK____",
        "_____KwWWWwK____",
        "______KwWWK_____",
        "_______KwK______",
        "_______KnK______",
        "______KnnK______",
        "________________",
    };

    static String[] PIP_MIECZE = {
        "________________",
        "_______K________",
        "______KwK_______",
        "_____KwwK_______",
        "____KwwwK_______",
        "___KwwwwK_______",
        "__KwwwwwK_______",
        "__KwwwwwwK______",
        "___KwwwwwK______",
        "____KwwwwK______",
        "_____KwwK_______",
        "______KwK_______",
        "_______gK_______",
        "______gKg_______",
        "_______g________",
        "________________",
    };

    static String[] PIP_MONETY = {
        "________________",
        "______KKK_______",
        "_____KgggK______",
        "____KGGGGgK_____",
        "___KGGggGGgK____",
        "___KGGgGGGgK____",
        "___KGGGGGGgK____",
        "___KGGGGGGgK____",
        "___KGGgGGGgK____",
        "___KGGggGGgK____",
        "____KGGGGgK_____",
        "_____KgggK______",
        "______KKK_______",
        "________________",
        "________________",
        "________________",
    };

    static String[] PIP_BULAWY = {
        "________________",
        "______KKK_______",
        "_____KrrrK______",
        "____KRRRRrK_____",
        "___KRRRRRRrK____",
        "___KRRoRRoRK____",
        "___KRRRRRRRRK___",
        "___KRroRRroRK___",
        "___KRRRRRRRRK___",
        "___KRRRRRRRRK___",
        "____KRRRRRK_____",
        "_____KrrrK______",
        "______KnK_______",
        "______KnK_______",
        "_____KnnK_______",
        "________________",
    };

    // ==================== COURTS 16×16 ====================

    static String[] COURT_JOPEK = {
        "________________",
        "_______gg_______",
        "______gGGg______",
        "_____gGGGGg_____",
        "____xGxxxxGx____",
        "____xGxxxxGx____",
        "_____xxxxxx_____",
        "______xn_nx_____",
        "_____KwwwwK_____",
        "____KwwwwwwK____",
        "____KwwwwwwK____",
        "_____KwwwwK_____",
        "______KwwK______",
        "______xK_Kx_____",
        "_____xK___Kx____",
        "________________",
    };

    static String[] COURT_RYCERZ = {
        "________________",
        "______gggg______",
        "_____gSSSSg_____",
        "____gSSSSSSg____",
        "____gSwSSwSg____",
        "____gSSSSSSg____",
        "_____SxxxxS_____",
        "______xn_nx_____",
        "_____SwwwwS_____",
        "____SwwwwwwS____",
        "____SwwwwwwS____",
        "_____SwwwwS_____",
        "______SwwS______",
        "______xS_Sx_____",
        "_____xS___Sx____",
        "________________",
    };

    static String[] COURT_KROLOWA = {
        "________________",
        "_____g_gg_g_____",
        "____gGGGGGGg____",
        "___gGGGGGGGGg___",
        "___gGxGGGxGGg___",
        "___gGGGGGGGGg___",
        "____xGxxxGx_____",
        "_____xn__nx_____",
        "_____KwwwwK_____",
        "____KwwwwwwK____",
        "____KwwwwwwK____",
        "_____KwwwwK_____",
        "______KwwK______",
        "______xK_Kx_____",
        "_____xK___Kx____",
        "________________",
    };

    static String[] COURT_KROL = {
        "________________",
        "___g_gg_gg_g____",
        "___gGGGGGGGg____",
        "___gGGGGGGGg____",
        "___gGxGGGxGGg___",
        "___gGGGGGGGg____",
        "____xGxxxGx_____",
        "_____xn__nx_____",
        "_____KwwwwK_____",
        "____KwwwwwwK____",
        "____KwwwwwwK____",
        "_____KwwwwK_____",
        "______KwwK______",
        "______xK_Kx_____",
        "_____xK___Kx____",
        "________________",
    };

    static String[] COURT_AS = {
        "________________",
        "_______g________",
        "______gGg_______",
        "_____gGGGg______",
        "____gGGGGGg_____",
        "___gGGGGGGGg____",
        "___gGGgGgGGg____",
        "___gGGGGGGGg____",
        "____gGGGGGg_____",
        "_____gGGGg______",
        "______gGg_______",
        "_______g________",
        "_______g________",
        "______gGg_______",
        "_______g________",
        "________________",
    };

    // ==================== HEROES B 16×16 ====================

    // Papiez V - wysoka mitra, zloty kij, biale szaty, broda
    static String[] HERO_PAPIEZ = {
        "_______gg_______",
        "______gGGg______",
        "______gGGg______",
        "_____gGGGGg_____",
        "_____xWWWWx_____",
        "_____xWxxWx_____",
        "______xn_nx_____",
        "______gwwg______",
        "_____gwwwwg_____",
        "____gwwwwwwg____",
        "____gwwwwwwg____",
        "_____gwwwwg_____",
        "______gwwg______",
        "______gw_g______",
        "_____gw___g_____",
        "____gw_____g____",
    };

    // Kochankowie VI - dwie postacie (czerwona + niebieska), serca miedzy nimi
    static String[] HERO_KOCHANKOWIE = {
        "________________",
        "___r__rrr__b____",
        "___rRrRrRr_b____",
        "___rRrrrRrRb____",
        "____xr__xr______",
        "____xn__xn______",
        "____rr__bb______",
        "___rRRrrbbB_____",
        "___rRRrrbbB_____",
        "___rRRrrbbB_____",
        "____rr__bb______",
        "____xr__xr______",
        "____xR__xBx_____",
        "____xR__xBx_____",
        "_____x___x______",
        "________________",
    };

    // Rydwan VII - zbrojny z helmym, tarcza, miecz, szersza sylwetka
    static String[] HERO_RYDWAN = {
        "_______ssss_____",
        "______sSSSSs____",
        "______sWxxWs____",
        "______sWxxWs____",
        "_______xn_nx____",
        "____sswwwwwwss__",
        "___sSwSSSSSSws__",
        "___sSwSSSSSSws__",
        "____sSSSSSSSSs__",
        "____sSSSSSSSSs__",
        "______sSSSSs____",
        "______sSSSSs____",
        "______xs__Sx____",
        "_____xs____Sx___",
        "____xs______Sx__",
        "________________",
    };

    // Sprawiedliwosc VIII - zaslepienie, waga w jednej rece, miecz w drugiej
    static String[] HERO_SPRAWIEDLIWOSC = {
        "________________",
        "_______zz_______",
        "______zZZz______",
        "______zZZz______",
        "______xrrrx_____",
        "_______xn_nx____",
        "_____gwwwwwwg___",
        "____gwwwwwwwwg__",
        "___gwwwwwwwwwwg_",
        "___gwwwwwwwwwwg_",
        "____gwwwwwwwwg__",
        "_____gwwwwwwg___",
        "______gw__wg____",
        "______gw__wg____",
        "_____gw____wg___",
        "____gw______wg__",
    };

    // Pustelnik IX - kaptur, latarnia, kij, dlugi plaszcz
    static String[] HERO_PUSTERNIK = {
        "______PPP_______",
        "_____PPPPp______",
        "_____PPPPp______",
        "____pPPPPp______",
        "____pWxxWp______",
        "____pWxxWp______",
        "_____xn_nx______",
        "_____KwwK_______",
        "____KwwwwK______",
        "___KwwwwwwK_____",
        "___KwwwwwwK_____",
        "___KwwwwwwK_____",
        "____KwwwwK______",
        "____Kw__Kw______",
        "____Kw__Kw______",
        "____Kn__Kn______",
    };

    // ==================== TLA 50×37 (×16 = 800×592) ====================

    static String[] BG_KIELICHY = generateBg(50, 37, 'B', 'b', 'k', 'K', new char[]{'w', 'W', 's'});
    static String[] BG_MIECZE = generateBg(50, 37, 'C', 'c', 'k', 'K', new char[]{'s', 'S', 'w'});
    static String[] BG_MONETY = generateBg(50, 37, 'O', 'o', 'k', 'K', new char[]{'g', 'G', 'n'});
    static String[] BG_BULAWY = generateBg(50, 37, 'R', 'r', 'k', 'K', new char[]{'o', 'O', 'g'});

    // Menu - mistyczny tarot: ciemny fiolet + zlote symbole + gwiazdki
    static String[] BG_MENU = generateMenuBg(50, 37);

    // Lobby - kamienna piwnica: szarobeżowe kamienie
    static String[] BG_LOBBY = generateLobbyBg(50, 37);

    // ==================== HELPERS ====================

    private static String[] generateBg(int w, int h, char c1, char c2, char dk, char md, char[] accents) {
        Random rng = new Random(42);
        String[] rows = new String[h];
        for (int y = 0; y < h; y++) {
            StringBuilder sb = new StringBuilder(w);
            for (int x = 0; x < w; x++) {
                double dist = Math.sqrt(Math.pow(x - w / 2.0, 2) + Math.pow(y - h / 2.0, 2));
                double maxDist = Math.sqrt(w * w + h * h) / 2.0;
                double t = dist / maxDist;
                char ch;
                double roll = rng.nextDouble();
                if (t > 0.7) {
                    ch = dk;
                } else if (t > 0.4) {
                    ch = md;
                } else if (roll < 0.04) {
                    ch = accents[rng.nextInt(accents.length)];
                } else if (roll < 0.12) {
                    ch = c2;
                } else {
                    ch = c1;
                }
                // stone pattern: occasional mortar lines
                if ((x % 8 == 0 || y % 6 == 0) && rng.nextDouble() < 0.3) {
                    ch = dk;
                }
                sb.append(ch);
            }
            rows[y] = sb.toString();
        }
        return rows;
    }

    // Menu: ciemny fiolet + zlote gwiazdki + tarotowe symbole
    private static String[] generateMenuBg(int w, int h) {
        Random rng = new Random(13);
        String[] rows = new String[h];
        for (int y = 0; y < h; y++) {
            StringBuilder sb = new StringBuilder(w);
            for (int x = 0; x < w; x++) {
                double dist = Math.sqrt(Math.pow(x - w / 2.0, 2) + Math.pow(y - h / 2.0, 2));
                double maxDist = Math.sqrt(w * w + h * h) / 2.0;
                double t = dist / maxDist;
                char ch;
                double roll = rng.nextDouble();
                if (t > 0.75) {
                    ch = 'D';
                } else if (t > 0.5) {
                    ch = 'P';
                } else if (roll < 0.02) {
                    ch = 'G';
                } else if (roll < 0.06) {
                    ch = 'g';
                } else if (roll < 0.1) {
                    ch = 'E';
                } else {
                    ch = 'p';
                }
                // tarot frame lines
                if ((x == 2 || x == w - 3 || y == 1 || y == h - 2) && t < 0.8) {
                    ch = 'g';
                }
                // cross in center
                if (Math.abs(x - w / 2) <= 1 && Math.abs(y - h / 2) <= 4 && t < 0.5) {
                    ch = 'G';
                }
                if (Math.abs(y - h / 2) <= 1 && Math.abs(x - w / 2) <= 4 && t < 0.5) {
                    ch = 'G';
                }
                sb.append(ch);
            }
            rows[y] = sb.toString();
        }
        return rows;
    }

    // Lobby: kamienna piwnica, ciepły szarobeż
    private static String[] generateLobbyBg(int w, int h) {
        Random rng = new Random(77);
        String[] rows = new String[h];
        for (int y = 0; y < h; y++) {
            StringBuilder sb = new StringBuilder(w);
            for (int x = 0; x < w; x++) {
                double dist = Math.sqrt(Math.pow(x - w / 2.0, 2) + Math.pow(y - h / 2.0, 2));
                double maxDist = Math.sqrt(w * w + h * h) / 2.0;
                double t = dist / maxDist;
                char ch;
                double roll = rng.nextDouble();
                if (t > 0.7) {
                    ch = 'K';
                } else if (t > 0.45) {
                    ch = 'C';
                } else if (roll < 0.03) {
                    ch = 'g';
                } else if (roll < 0.1) {
                    ch = 'c';
                } else if (roll < 0.18) {
                    ch = 'n';
                } else {
                    ch = 'N';
                }
                // stone mortar
                if ((x % 7 == 0 || y % 5 == 0) && rng.nextDouble() < 0.35) {
                    ch = 'K';
                }
                sb.append(ch);
            }
            rows[y] = sb.toString();
        }
        return rows;
    }

    // ==================== SHEET COMPOSER ====================

    private static void saveIcon(String[] template, int size, String path) throws Exception {
        int tSize = template.length;
        int scale = size / tSize;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        for (int ty = 0; ty < tSize; ty++) {
            String row = template[ty];
            for (int tx = 0; tx < row.length(); tx++) {
                char c = row.charAt(tx);
                int[] rgb = PAL.get(c);
                if (rgb == null) continue;
                g.setColor(new Color(rgb[0], rgb[1], rgb[2], rgb[3]));
                g.fillRect(tx * scale, ty * scale, scale, scale);
            }
        }
        g.dispose();
        ImageIO.write(img, "png", new File(path));
    }

    private static void composeSheet(String[][] templates, int cols, int rows,
                                     int cellSize, String path) throws Exception {
        int w = cols * cellSize, h = rows * cellSize;
        BufferedImage sheet = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = sheet.createGraphics();
        for (int i = 0; i < templates.length; i++) {
            int col = i % cols, row = i / cols;
            int tSize = templates[i].length;
            int scale = cellSize / tSize;
            for (int ty = 0; ty < tSize; ty++) {
                String r = templates[i][ty];
                for (int tx = 0; tx < r.length(); tx++) {
                    char c = r.charAt(tx);
                    int[] rgb = PAL.get(c);
                    if (rgb == null) continue;
                    g.setColor(new Color(rgb[0], rgb[1], rgb[2], rgb[3]));
                    g.fillRect(col * cellSize + tx * scale, row * cellSize + ty * scale, scale, scale);
                }
            }
        }
        g.dispose();
        ImageIO.write(sheet, "png", new File(path));
    }

    private static void saveTiled(String[] template, int tileW, int tileH,
                                  int outW, int outH, String path) throws Exception {
        BufferedImage img = new BufferedImage(outW, outH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        int scaleX = outW / tileW;
        int scaleY = outH / tileH;
        for (int ty = 0; ty < tileH; ty++) {
            String row = template[ty];
            for (int tx = 0; tx < row.length(); tx++) {
                char c = row.charAt(tx);
                int[] rgb = PAL.get(c);
                if (rgb == null) continue;
                g.setColor(new Color(rgb[0], rgb[1], rgb[2], rgb[3]));
                g.fillRect(tx * scaleX, ty * scaleY, scaleX, scaleY);
            }
        }
        g.dispose();
        ImageIO.write(img, "png", new File(path));
    }

    // ==================== MAIN ====================

    public static void main(String[] args) throws Exception {
        String base = "C:\\JavaProjects\\TarotCrawlerRPG\\assets";
        new File(base + "\\icons").mkdirs();
        new File(base + "\\foes").mkdirs();
        new File(base + "\\heroes").mkdirs();
        new File(base + "\\bosses").mkdirs();
        new File(base + "\\bg").mkdirs();

        // --- Ikony 32×32 ---
        saveIcon(ICON_HELM,    32, base + "\\icons\\helm.png");
        saveIcon(ICON_ARMOR,   32, base + "\\icons\\armor.png");
        saveIcon(ICON_BOOTS,   32, base + "\\icons\\boots.png");
        saveIcon(ICON_SWORD,   32, base + "\\icons\\sword.png");
        saveIcon(ICON_SHIELD,  32, base + "\\icons\\shield.png");
        saveIcon(ICON_AMULET,  32, base + "\\icons\\amulet.png");
        saveIcon(ICON_WAND,    32, base + "\\icons\\wand.png");
        saveIcon(ICON_KEY,     32, base + "\\icons\\key.png");

        // --- Pips 512×128 (4×1, 128px/klatka) ---
        composeSheet(new String[][]{
            PIP_KIELICHY, PIP_MIECZE, PIP_MONETY, PIP_BULAWY},
            4, 1, 128, base + "\\foes\\pips.png");

        // --- Courts 640×128 (5×1, 128px/klatka) ---
        composeSheet(new String[][]{
            COURT_JOPEK, COURT_RYCERZ, COURT_KROLOWA, COURT_KROL, COURT_AS},
            5, 1, 128, base + "\\foes\\courts.png");

        // --- Heroes B 384×640 (3×5, 128px/klatka) ---
        String[][] heroTpl = {
            HERO_PAPIEZ, HERO_KOCHANKOWIE, HERO_RYDWAN, HERO_SPRAWIEDLIWOSC, HERO_PUSTERNIK
        };
        String[][] heroesB = new String[15][];
        for (int i = 0; i < 5; i++) {
            heroesB[i * 3]     = heroTpl[i];
            heroesB[i * 3 + 1] = heroTpl[i];
            heroesB[i * 3 + 2] = heroTpl[i];
        }
        composeSheet(heroesB, 3, 5, 128, base + "\\heroes\\sheet-b.png");

        // --- Bosses 384×640 (3×5, 128px/klatka) ---
        String[][] bosses = new String[15][];
        for (int i = 0; i < 5; i++) {
            bosses[i * 3]     = heroTpl[i];
            bosses[i * 3 + 1] = heroTpl[i];
            bosses[i * 3 + 2] = heroTpl[i];
        }
        composeSheet(bosses, 3, 5, 128, base + "\\bosses\\sheet.png");

        // --- Tla 800×600 (50×37 ×16) ---
        saveTiled(BG_KIELICHY, 50, 37, 800, 600, base + "\\bg\\kielichy.png");
        saveTiled(BG_MIECZE,   50, 37, 800, 600, base + "\\bg\\miecze.png");
        saveTiled(BG_MONETY,   50, 37, 800, 600, base + "\\bg\\monety.png");
        saveTiled(BG_BULAWY,   50, 37, 800, 600, base + "\\bg\\bulawy.png");
        saveTiled(BG_MENU,     50, 37, 800, 600, base + "\\bg\\menu.png");
        saveTiled(BG_LOBBY,    50, 37, 800, 600, base + "\\bg\\lobby.png");

        System.out.println("PixelArt: wszystko wygenerowane");
    }
}
