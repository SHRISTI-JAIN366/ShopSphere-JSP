@echo off
setlocal enabledelayedexpansion
title ShopSphere Automated Test Suite
chcp 65001 > nul

echo ========================================================
echo   Compiling & Running ShopSphere Automated Tests...
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

java -Dfile.encoding=UTF-8 -cp out com.shopsphere.test.ShopSphereTest
pause
