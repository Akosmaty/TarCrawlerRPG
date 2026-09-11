@echo off
REM BOT - test manualny: bot gra sam (mapa, bitwa, loot, awanse). Wszystko w logu.
REM Uruchom z katalogu projektu: run_bot.bat
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

echo == BOT gra (log: %%TEMP%%\tarot-test-home\TarotCrawlerRPG\game.log) ==
"%JAVABIN%\java.exe" "-Djava.awt.headless=true" "-Duser.home=%TEMP%\tarot-test-home" -cp "%PROJ%\out;%PROJ%\test-out" com.tarotcrawler.TestRunner com.tarotcrawler.bot.BotPlayTest
echo.
echo == ogon loga bota ==
powershell -NoProfile -Command "Get-Content \"$env:TEMP\tarot-test-home\TarotCrawlerRPG\game.log\" | Select-Object -Last 12"
