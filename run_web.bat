@echo off
setlocal enabledelayedexpansion
title ShopSphere Localhost Web Server (http://localhost:8080)
chcp 65001 > nul

echo ========================================================
echo   Starting ShopSphere Localhost Web Server...
echo ========================================================

if not exist out mkdir out

if exist sources.txt del sources.txt
for /r src %%f in (*.java) do (
    set "fpath=%%f"
    set "fpath=!fpath:\=/!"
    echo "!fpath!">>sources.txt
)

javac -encoding UTF-8 -d out @sources.txt
if exist sources.txt del sources.txt

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo Launching Google Chrome to http://localhost:8080 ...
start chrome http://localhost:8080/ 2>nul || start http://localhost:8080/

java -Dfile.encoding=UTF-8 -cp out com.shopsphere.app.ShopSphereWebApplication
pause
