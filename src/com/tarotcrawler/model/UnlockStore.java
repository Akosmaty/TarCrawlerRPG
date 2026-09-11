package com.tarotcrawler.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/** Zapis odblokowanych klas-bossow w katalogu domowym gracza. */
public final class UnlockStore {

    private UnlockStore() {}

    private static Path path() {
        return Paths.get(System.getProperty("user.home"), "TarotCrawlerRPG", "unlocks.txt");
    }

    public static Set<String> load() {
        try {
            Path p = path();
            if (!Files.exists(p)) {
                return new HashSet<>();
            }
            Set<String> out = new HashSet<>();
            for (String line : Files.readAllLines(p)) {
                line = line.trim();
                if (!line.isEmpty()) {
                    out.add(line);
                }
            }
            return out;
        } catch (IOException e) {
            return new HashSet<>();
        }
    }

    public static void save(Set<String> ids) {
        try {
            Path p = path();
            Files.createDirectories(p.getParent());
            Files.write(p, new ArrayList<>(ids));
        } catch (IOException ignored) {
        }
    }
}
