# ShopSphere Enterprise E-Commerce System PowerShell Runner
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "  Compiling & Launching ShopSphere E-Commerce System... " -ForegroundColor Green
Write-Host "========================================================" -ForegroundColor Cyan

if (-not (Test-Path "out")) {
    New-Item -ItemType Directory -Path "out" | Out-Null
}

$files = (Get-ChildItem -Recurse -Filter *.java src).FullName
javac -encoding UTF-8 -d out $files

if ($LASTEXITCODE -eq 0) {
    Clear-Host
    java "-Dfile.encoding=UTF-8" -cp out com.shopsphere.app.ShopSphereApplication
} else {
    Write-Host "[ERROR] Compilation failed!" -ForegroundColor Red
}
