@echo off
REM Testy jednostkowe TarotCrawlerRPG (bez zaleznosci, UI headless).
REM Uruchom z katalogu projektu: run_tests.bat
setlocal
set JAVABIN=C:\Program Files\JetBrains\IntelliJ IDEA 2025.3.3\jbr\bin
set PROJ=C:\JavaProjects\TarotCrawlerRPG

echo == kompilacja src ==
for /R "%PROJ%\src" %%F in (*.java) do "%JAVABIN%\javac.exe" -encoding UTF-8 -cp "%PROJ%\out" -d "%PROJ%\out" "%%F"
if errorlevel 1 exit /b 1

echo == kompilacja testow ==
if not exist "%PROJ%\test-out" mkdir "%PROJ%\test-out"
"%JAVABIN%\javac.exe" -encoding UTF-8 -cp "%PROJ%\out" -d "%PROJ%\test-out" "%PROJ%\test\com\tarotcrawler\TestRunner.java"
if errorlevel 1 exit /b 1
for /R "%PROJ%\test" %%F in (*Test.java) do "%JAVABIN%\javac.exe" -encoding UTF-8 -cp "%PROJ%\out;%PROJ%\test-out" -d "%PROJ%\test-out" "%%F"
if errorlevel 1 exit /b 1

echo == testy ==
"%JAVABIN%\java.exe" "-Djava.awt.headless=true" "-Duser.home=%TEMP%\tarot-test-home" -cp "%PROJ%\out;%PROJ%\test-out" com.tarotcrawler.TestRunner com.tarotcrawler.model.HeroAttrTest com.tarotcrawler.model.GearHandsTest com.tarotcrawler.model.EnemyTableTest com.tarotcrawler.model.LootTableTest com.tarotcrawler.model.RunStateTest com.tarotcrawler.model.SaveDataTest com.tarotcrawler.model.SpellRegistryTest com.tarotcrawler.ui.DungeonLootUiTest com.tarotcrawler.ui.BattleStartTest com.tarotcrawler.util.AssetsSheetTest
