# ShopSphere Localhost Web Server (http://localhost:8080) PowerShell Runner
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "  Starting ShopSphere Web Server on http://localhost:8080" -ForegroundColor Green
Write-Host "========================================================" -ForegroundColor Cyan

if (-not (Test-Path "out")) {
    New-Item -ItemType Directory -Path "out" | Out-Null
}

$files = (Get-ChildItem -Recurse -Filter *.java src).FullName
javac -encoding UTF-8 -d out $files

if ($LASTEXITCODE -eq 0) {
    Write-Host "Opening Google Chrome to http://localhost:8080/ ..." -ForegroundColor Yellow
    Start-Process "chrome.exe" "http://localhost:8080/" -ErrorAction SilentlyContinue
    if (-not $?) {
        Start-Process "http://localhost:8080/"
    }
    java "-Dfile.encoding=UTF-8" -cp out com.shopsphere.app.ShopSphereWebApplication
} else {
    Write-Host "[ERROR] Compilation failed!" -ForegroundColor Red
}
