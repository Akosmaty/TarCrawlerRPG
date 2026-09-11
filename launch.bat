@echo off
cd /d "C:\JavaProjects\TarotCrawlerRPG"
taskkill /f /im java.exe >nul 2>&1
echo Kompilacja...
"C:\Users\Adam\.jdks\openjdk-25.0.2\bin\javac.exe" -encoding UTF-8 -d out src\CheckImages.java src\com\tarotcrawler\Main.java src\com\tarotcrawler\model\*.java src\com\tarotcrawler\ui\*.java src\com\tarotcrawler\util\*.java
if %errorlevel% neq 0 (
    echo Blad kompilacji!
    pause
    exit /b 1
)
echo Uruchamianie...
"C:\Users\Adam\.jdks\openjdk-25.0.2\bin\java.exe" -cp out com.tarotcrawler.Main
