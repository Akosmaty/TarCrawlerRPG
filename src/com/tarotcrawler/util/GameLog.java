package com.tarotcrawler.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/** Prosty logger plikowy: ~/TarotCrawlerRPG/game.log (+ game-prev.log z poprzedniego runu).
 *  Lapie tez nieprzechwycone wyjatki razem ze stosem wywolan. */
public final class GameLog {

    public enum Level { DEBUG, INFO, WARN, ERROR }

    private static Path path;
    private static volatile Level level = Level.DEBUG;
    private static final BlockingQueue<String> QUEUE = new LinkedBlockingQueue<>();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    private GameLog() {}

    public static synchronized void init() {
        if (path != null) {
            return;
        }
        try {
            Path dir = Paths.get(System.getProperty("user.home"), "TarotCrawlerRPG");
            Files.createDirectories(dir);
            Path prev = dir.resolve("game-prev.log");
            path = dir.resolve("game.log");
            try {
                Files.deleteIfExists(prev);
            } catch (IOException ignored) {
            }
            if (Files.exists(path)) {
                try {
                    Files.move(path, prev);
                } catch (IOException ignored) {
                }
            }
            Thread worker = new Thread(GameLog::drain, "gamelog");
            worker.setDaemon(true);
            worker.start();
            Thread.setDefaultUncaughtExceptionHandler(
                    (t, e) -> error("UNCAUGHT w wątku " + t.getName(), e));
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                QUEUE.offer("[" + java.time.LocalDateTime.now().format(FMT) + "] INFO === zamkniecie gry ===");
                try {
                    Thread.sleep(400);
                } catch (InterruptedException ignored) {
                }
            }, "gamelog-shutdown"));
            startWatchdog();
            info("=== start gry ===");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Path logPath() {
        return path;
    }

    private static void drain() {
        try {
            while (true) {
                String line = QUEUE.take();
                try {
                    Files.writeString(path, line + System.lineSeparator(),
                            StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                } catch (IOException ignored) {
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** Watchdog: jesli EDT nie odpowiada 10 s, zrzuca stosy wszystkich watkow do loga. */
    private static void startWatchdog() {
        Thread watchdog = new Thread(() -> {
            int missed = 0;
            while (true) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    return;
                }
                final boolean[] alive = {false};
                try {
                    javax.swing.SwingUtilities.invokeLater(() -> alive[0] = true);
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    return;
                }
                if (!alive[0] && path != null) {
                    missed++;
                    if (missed >= 5) {
                        dumpThreads();
                        missed = 0;
                    }
                } else {
                    missed = 0;
                }
            }
        }, "gamelog-watchdog");
        watchdog.setDaemon(true);
        watchdog.start();
    }

    private static void dumpThreads() {
        StringBuilder sb = new StringBuilder("WATCHDOG: EDT nie odpowiada! Zrzut watkow:");
        for (java.util.Map.Entry<Thread, StackTraceElement[]> e : Thread.getAllStackTraces().entrySet()) {
            sb.append("\n--- ").append(e.getKey().getName()).append(" ---");
            StackTraceElement[] st = e.getValue();
            for (int i = 0; i < Math.min(25, st.length); i++) {
                sb.append("\n    at ").append(st[i]);
            }
            if (sb.length() > 9000) {
                break;
            }
        }
        log(Level.ERROR, sb.toString());
    }

    private static void log(Level l, String msg) {
        if (l.ordinal() < level.ordinal() || path == null) {
            return;
        }
        QUEUE.offer("[" + LocalDateTime.now().format(FMT) + "] " + l + " " + msg);
    }

    public static void debug(String msg) { log(Level.DEBUG, msg); }
    public static void info(String msg) { log(Level.INFO, msg); }
    public static void warn(String msg) { log(Level.WARN, msg); }
    public static void error(String msg) { log(Level.ERROR, msg); }

    public static void error(String msg, Throwable t) {
        StringBuilder sb = new StringBuilder(msg);
        int total = 0;
        for (Throwable c = t; c != null && total < 8000; c = c.getCause()) {
            sb.append(" <- ").append(c);
            for (StackTraceElement el : c.getStackTrace()) {
                sb.append("\n    at ").append(el);
                total = sb.length();
                if (total >= 8000) {
                    break;
                }
            }
            if (c.getCause() == c) {
                break;
            }
        }
        log(Level.ERROR, sb.toString());
    }
}
