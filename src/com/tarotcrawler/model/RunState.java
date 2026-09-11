package com.tarotcrawler.model;

import java.util.ArrayList;
import java.util.List;

/** Stan runu: DRUZYNA Hero z osobnymi lvl/exp, wspolny loch. Sprzet kazdego tylko na run. */
public class RunState {
    private final List<Hero> party;
    private final Dungeon dungeon;
    private int floor = 1;
    private String lastBossId;

    public RunState(List<Hero> party, Dungeon dungeon) {
        if (party == null || party.isEmpty()) {
            throw new IllegalArgumentException("Druzyna nie moze byc pusta");
        }
        if (party.size() > 4) {
            throw new IllegalArgumentException("Druzyna max 4 postacie");
        }
        this.party = new ArrayList<>(party);
        this.dungeon = dungeon;
    }

    public List<Hero> getParty() { return new ArrayList<>(party); }
    public Hero getLeader() { return party.get(0); }
    public Dungeon getDungeon() { return dungeon; }
    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }
    public String getLastBossId() { return lastBossId; }
    public void setLastBossId(String lastBossId) { this.lastBossId = lastBossId; }

    public boolean hasHero(HeroClass h) {
        for (Hero hero : party) {
            if (hero.getTemplate() == h) {
                return true;
            }
        }
        return false;
    }

    public boolean counters(Dungeon d) {
        for (Hero hero : party) {
            if (hero.getTemplate().getStrongAgainst().equals(d.name())) {
                return true;
            }
        }
        return false;
    }

    public String partySymbols() {
        StringBuilder sb = new StringBuilder();
        for (Hero hero : party) {
            sb.append(hero.getTemplate().getSymbol()).append(hero.getLevel()).append(" ");
        }
        return sb.toString().trim();
    }

    public int power() {
        int s = 0;
        for (Hero hero : party) {
            s += hero.power();
        }
        return s;
    }

    public int defense() {
        int s = 0;
        for (Hero hero : party) {
            s += hero.defense();
        }
        return s / Math.max(1, party.size());
    }

    public int maxHp() {
        int s = 0;
        for (Hero hero : party) {
            s += hero.maxHp();
        }
        return s;
    }

    public static int nextLevelExp(int level) {
        return (int) (100 * Math.pow(level, 1.5));
    }
}
