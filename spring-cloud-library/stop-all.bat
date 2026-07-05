@echo off
title Library System - Stop All
echo ============================================
echo   Stopping All Services...
echo ============================================
echo.

echo [1] Stopping microservices by port...
for %%p in (8080 8081 8082 8083 8084 8085) do (
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":%%p.*LISTENING" 2^>nul') do (
        echo   Killing port %%p (PID: %%a)
        taskkill /f /pid %%a 2>nul
    )
)

echo.
echo [2] Stopping frontend...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":5173.*LISTENING" 2^>nul') do (
    echo   Killing port 5173 (PID: %%a)
    taskkill /f /pid %%a 2>nul
)

echo.
echo [3] Stopping Sentinel Dashboard (8090)...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8090.*LISTENING" 2^>nul') do (
    echo   Killing port 8090 (PID: %%a)
    taskkill /f /pid %%a 2>nul
)

echo.
echo [4] Stopping Nacos (8848)...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8848.*LISTENING" 2^>nul') do (
    echo   Killing port 8848 (PID: %%a)
    taskkill /f /pid %%a 2>nul
)

echo.
echo ============================================
echo   All services stopped.
echo ============================================
pause
