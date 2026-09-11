package com.tarotcrawler.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Auto-zapis gry: unlocki, wyczyszczone lochy, druzyna, lvl/exp kazdej postaci. */
public final class SaveData {

    public Set<String> unlocked = new HashSet<>();
    public Set<Dungeon> cleared = EnumSet.noneOf(Dungeon.class);
    public List<String> party = new ArrayList<>();
    /** nazwa klasy -> [level, exp, pool] */
    public Map<String, int[]> heroes = new HashMap<>();
    /** nazwa klasy -> id czarow */
    public Map<String, List<String>> spells = new HashMap<>();

    private SaveData() {}

    /** Pusty zapis (m.in. do testow). */
    static SaveData create() {
        return new SaveData();
    }

    private static Path path() {
        return Paths.get(System.getProperty("user.home"), "TarotCrawlerRPG", "save.txt");
    }

    public static SaveData load() {
        SaveData d = new SaveData();
        try {
            Path p = path();
            if (!Files.exists(p)) {
                d.unlocked = UnlockStore.load();
                return d;
            }
            for (String raw : Files.readAllLines(p)) {
                String line = raw.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                int eq = line.indexOf('=');
                if (eq < 0) {
                    continue;
                }
                String key = line.substring(0, eq).trim();
                String val = line.substring(eq + 1).trim();
                if (key.equals("unlocked")) {
                    for (String s : val.split(",")) {
                        s = s.trim();
                        if (!s.isEmpty()) {
                            d.unlocked.add(s);
                        }
                    }
                } else if (key.equals("cleared")) {
                    for (String s : val.split(",")) {
                        s = s.trim();
                        if (s.isEmpty()) {
                            continue;
                        }
                        try {
                            d.cleared.add(Dungeon.valueOf(s));
                        } catch (IllegalArgumentException ignored) {
                        }
                    }
                } else if (key.equals("party")) {
                    for (String s : val.split(",")) {
                        s = s.trim();
                        if (!s.isEmpty()) {
                            d.party.add(s);
                        }
                    }
                } else if (key.startsWith("spells.")) {
                    String name = key.substring(7);
                    List<String> ids = new ArrayList<>();
                    for (String s : val.split(",")) {
                        s = s.trim();
                        if (!s.isEmpty()) {
                            ids.add(s);
                        }
                    }
                    if (!ids.isEmpty()) {
                        d.spells.put(name, ids);
                    }
                } else if (key.startsWith("hero.")) {
                    String name = key.substring(5);
                    String[] parts = val.split(",");
                    try {
                        int lvl = Math.max(1, Integer.parseInt(parts[0].trim()));
                        int exp = parts.length > 1 ? Math.max(0, Integer.parseInt(parts[1].trim())) : 0;
                        int pool = parts.length > 2 ? Math.max(0, Integer.parseInt(parts[2].trim())) : 0;
                        d.heroes.put(name, new int[]{lvl, exp, pool});
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        } catch (IOException e) {
            d.unlocked = UnlockStore.load();
        }
        return d;
    }

    public boolean hasProgress() {
        return !unlocked.isEmpty() || !cleared.isEmpty() || !party.isEmpty() || !heroes.isEmpty();
    }

    public void clearAll() {
        unlocked.clear();
        cleared.clear();
        party.clear();
        heroes.clear();
        spells.clear();
    }

    public void save() {
        try {
            Path p = path();
            Files.createDirectories(p.getParent());
            List<String> lines = new ArrayList<>();
            lines.add("# TarotCrawlerRPG save - nie edytuj");
            lines.add("unlocked=" + String.join(",", new ArrayList<>(unlocked)));
            List<String> cl = new ArrayList<>();
            for (Dungeon d : cleared) {
                cl.add(d.name());
            }
            lines.add("cleared=" + String.join(",", cl));
            lines.add("party=" + String.join(",", party));
            List<String> names = new ArrayList<>(heroes.keySet());
            java.util.Collections.sort(names);
            for (String n : names) {
                int[] le = heroes.get(n);
                int pool = le.length > 2 ? le[2] : 0;
                lines.add("hero." + n + "=" + le[0] + "," + le[1] + "," + pool);
            }
            List<String> snames = new ArrayList<>(spells.keySet());
            java.util.Collections.sort(snames);
            for (String n : snames) {
                lines.add("spells." + n + "=" + String.join(",", spells.get(n)));
            }
            Files.write(p, lines);
        } catch (IOException ignored) {
        }
    }
}
