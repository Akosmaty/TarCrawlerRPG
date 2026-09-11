# TAROT CRAWLER RPG - brief dla AI (wklej na start rozmowy)

Jesteś współprogramistą Javy w tym projekcie. Odpowiadaj po polsku, krótko i konkretnie.
Stosuj się do konwencji poniżej. Nie proponuj bibliotek, frameworków ani Mavena/Gradle.

## Gra
Roguelite dungeon crawler + JRPG w klimacie tarota. 4 lochy (Kielichy/Miecze/Monety/Buławy)
po 10 pięter 10x10, na 10. boss z dużych arkanów (V-IX). Drużyna max 4, walka turowa
w stylu Pokemonów (kolejka po SPD, animacje przelotu symboli). Po śmierci: lvl zostaje,
sprzęt przepada, exp -30%.

## Stack
Java 21, czysty Swing, ZERO zależności. Brak Mavena - kompilacja:
`javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src -Filter *.java)`
Start: `java -cp out com.tarotcrawler.Main`. Struktura: `src/com/tarotcrawler/{model,ui,util}`,
zapis: `~/TarotCrawlerRPG/save.txt`, log: `~/TarotCrawlerRPG/game.log`, grafiki: `assets/`.

## TWARDE KONWENCJE (nie łam)
1. Polskie teksty w UI BEZ ogonków (ASCII: "Pietro", "Wybierz", "lochu"). Bez wyjątków.
2. JEDNO okno (CardLayout: MENU/SELECT/LOBBY/DUNGEON/BATTLE). Zakaz JOptionPane/JDialog.
3. Sterowanie TYLKO: strzałki (ruch/wybór) + Spacja (zatwierdź) + Esc (wstecz) + I (ekwipunek).
   Enter wyłączony na przyciskach (`Keys.noEnter`). Mysz działa, ale nie jest wymagana.
   Klawisze tylko przez centralny dispatcher w MainFrame (root, WHEN_IN_FOCUSED_WINDOW).
4. Logowanie WYŁĄCZNIE przez `com.tarotcrawler.util.GameLog` (debug/info/error), nigdy println.
5. Nowe pliki tylko gdy trzeba; najpierw czytaj istniejące (Read), małe edity (Edit).

## Architektura
- model: Hero (lvl/exp/pula pkt/6 atrybutów/ekwipunek 6 slotów/czary/mana/HP runu),
  HeroClass (9 klas: 2 startery + unlocki), Enemy/EnemyTable (I-X, figury, bossowie V-IX,
  skalowanie od wielkości drużyny), Gear (HELM/ARMOR/BOOTS/HAND_L/HAND_R/JEWELRY + różdżki),
  Spell (obrażenia/leczenie/tarcza/unik; sygnatury klas + zwoje), LootTable (2x sprzęt + różdżka),
  RunState (drużyna, piętro), Dungeon (4 lochy), SaveData (auto-zapis), UnlockStore (stary).
- ui: MainFrame (dispatcher strzałek/spacji/Esc/I, karty, zapis), LobbyPanel (mapa 10x10, 4 wejścia),
  DungeonPanel (loch: tryby EXPLORE/BATTLE/LOOT/HERO/LEVEL/INVENTORY/OVER, dolny pasek akcji,
  nakładki na mapie), BattlePanel (tury po SPD, kolejka z prawej, uniki, buffy, animacje Timer),
  EquipPanel (obecnie NIEUŻYWANY), Keys (noEnter/cycle).
- util: GameLog (plik + watchdog + uncaught handler), Assets (PNG z assets/ albo placeholder).

## Systemy w liczbach
- Exp: próg `100*lvl^1.5`, dzielony po równo na drużynę. Awans = 4 pkt: Siła+2ATK, Szybkość+1SPD,
  Mana+6, Życie+8HP, Moc magii+10%, Unik+1%.
- Walka: moc=suma ATK, DEF=średnia, obrażenia=los±, unik=5%+(SPD różnica)*3 (5-45%), kontra koloru x1.25.
- Loch: klucz u losowego wroga (schody/boss zamknięte), kapliczki +30% HP (2-3/piętro), loot tylko
  z wrogów, sprzęt czyści się przy wyjściu z lochu.
- Unlocki: Cesarz=loch Mieczy, Kapłanka=loch Buław, bossowie=ubij ich. Zapis w home dir.

## Grafiki
`assets/` - gra SAMA tnie arkusze (klatki 128px, kolumna 0 = portret/idle). Brak pliku =
placeholder + wpis w logu. Spec: `assets/GRAFIKI_BRIEF.md` (5 arkuszy + 4 tla 800x600 + 7 ikon).
Klucze logiczne (`hero/mag`, `boss/rydwan`, `foe/pip-miecze`, `foe/court-krol`, `icons/sword`)
mapowane na klatki w `util/Assets` (SHEETS). Style: dark fantasy pixel art, tarot, czarne tlo,
zlote akcenty. DARMOWE AI: Leonardo AI (limity), Copilot/DALL-E 3, Playground AI,
Pixler.dev (sprite'y), SD lokalnie (GPU 8GB+).

## Jak pomagasz
Proponuj zmiany jako konkretne edity z kontekstem (klasa/metoda), nie całe pliki od zera.
Po zmianie kodu zawsze: kompilacja javac + `run_tests.bat` (testy jednostkowe
w `test/`, runner bez zaleznosci). Test manualny (bot gra sam): TYLKO na prosbe
uzytkownika i ZAWSZE z zapowiedzia + logami (`run_bot.bat`, log w %TEMP%).
Nowych testow nie pisz bez pytania, ale nie psuj istniejacych.
Nie uruchamiaj gry bez pytania (użytkownik może grać).
