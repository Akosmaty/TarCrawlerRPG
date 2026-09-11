$javaBin = "C:\Program Files\JetBrains\IntelliJ IDEA 2025.3.3\jbr\bin"
if (!(Test-Path -LiteralPath "out")) { New-Item -ItemType Directory -Path "out" | Out-Null }
$files = Get-ChildItem -Recurse -LiteralPath "src" -Filter *.java | ForEach-Object { $_.FullName }
& "$javaBin\javac.exe" -encoding UTF-8 -d out @files
if ($?) { & "$javaBin\java.exe" -cp out com.tarotcrawler.Main }
