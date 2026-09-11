# TAROT CRAWLER RPG - brief graficzny (dla AI do obrazkow)

Gra tnie arkusze SAMA (klatka = kolumna/rzad w siatce). Rysuj dokladnie w siatce,
bez odstepow i cieni wystajacych poza klatke. Bez tekstu, liter i znakow wodnych.

## WSPOLNY STYL (doklej do kazdego prompta)
dark fantasy pixel art, tarot card style, black background, gold accents,
16-bit, centered, no text, no watermark
BIALE TLO: gra sama wycina biel od krawedzi (flood fill) - arkusze moga byc na bialym,
postac zostanie wycieta, jasne detale wewnatrz sa bezpieczne.

## 1. ARKUSZE POSTACI - klatka 128x128 px
Kolumny = pozy: 0 portret/idle, 1 atak, 2 ranny. Gra uzywa teraz kolumny 0,
reszta na przyszle animacje bitwy. Przezroczyste tlo.

### heroes/sheet-a.png - 384x512 (3 kol x 4 rzedy) - OPCJONALNIE
Rzedy: 0 Glupiec (wlochczyga z tobolkiem, zero tarota),
1 Mag (sztukmistrz z rozdzka), 2 Cesarz (tron, zbroja),
3 Kaplanka (welon, ksiezyc).
W GRZE JEST JUZ: `all/1-4 heros.jpg` (te same 4 postacie, gra tnie sama).
Ten plik zrob tylko jak chcesz podmienic styl.

### heroes/sheet-b.png - 384x640 (3 kol x 5 rzedow)
Rzedy: 0 Papiez V, 1 Kochankowie VI, 2 Rydwan VII, 3 Sprawiedliwosc VIII,
4 Pustelnik IX (latarnia).

## 2. WROGOWIE - klatka 128x128 px, przezroczyste tlo

### foes/pips.png - 512x128 (4 kol x 1 rzad)
Sludzy zywiolow, grozni mali towarzysze kart liczbowych I-X.
Kolumny: 0 duszek Kielichow (woda), 1 duszek Mieczy (powietrze),
2 duszek Monet (ziemia), 3 duszek Bulaw (ogien).

### foes/courts.png - 640x128 (5 kol x 1 rzad)
Dworzanie: 0 Jopek/Paz, 1 Rycerz, 2 Krolowa, 3 Krol, 4 As (czysta esencja koloru).

### bosses/sheet.png - 384x640 (3 kol x 5 rzedow)
Kolumny: 0 portret, 1 atak, 2 moc specjalna.
Rzedy: 0 Papiez V, 1 Kochankowie VI, 2 Rydwan VII, 3 Sprawiedliwosc VIII,
4 Pustelnik IX. Wiecej postaci i grozy niz zwykle wrogowie.

## 3. TLA LOCHOW - 800x600 px (gra ich JESZCZE nie wyswietla, podepniemy)
Mroczne, puste w srodku (tam leza kafle mapy), klimat koloru:
- bg/kielichy.png: zalane piwnice, niebieskosci
- bg/miecze.png: koszary/zamek, szarosci
- bg/monety.png: skarbiec/pustynia, zloto
- bg/bulawy.png: kuznia/wulkan, czerwienie

## 4. IKONY SPRZETU - 32x32 px, przezroczyste tlo
icons/helm.png, icons/armor.png, icons/boots.png, icons/sword.png,
icons/shield.png, icons/amulet.png, icons/wand.png
Proste czytelne piktogramy w stylu gry (zloto + kolor слоту).

## ROZMIARY WYSWIETLANIA (gra skaluje sama z arkuszy 128px)
- Bitwa (portrety bohaterow i wrogow): 64x64 px.
- Wybor druzyny (ikony klas): 48x48 px.
- Mapa lochu/lobby (znacznik gracza): sam symbol tekstowy (kafle siatki maja
  zmienny rozmiar, ikona by sie nie miescila) - bez zmian.
- Ikony sprzetu w loocie: 28x28 px.

## RAZEM: 5 arkuszy + 4 tla + 7 ikon = 16 plikow.

## DARMOWE AI (gra non-profit, tylko free)
- Leonardo AI (~150 tokenow/dzien): style growe, postacie, tekstury.
  Minus: na free sprawdz znak wodny i licencje.
- Copilot / Microsoft Designer (DALL-E 3, ~15 szybkich/dzien): portrety, zero instalacji.
- Playground AI (hojny dzienny limit): prosty UI.
- Pixler.dev (kilka dziennie, bez rejestracji): gotowe sprity 16-256px z alfa.
- Stable Diffusion lokalnie, np. NMKD GUI (Windows): bez limitu przy GPU 8GB+.
Zawsze sprawdz aktualny regulamin free (limity, watermark, komercja).
