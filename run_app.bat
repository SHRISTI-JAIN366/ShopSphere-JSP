@echo off
setlocal enabledelayedexpansion
title ShopSphere Enterprise E-Commerce System
chcp 65001 > nul

echo ========================================================
echo   Compiling ShopSphere E-Commerce System...
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

cls
java -Dfile.encoding=UTF-8 -cp out com.shopsphere.app.ShopSphereApplication
pause
