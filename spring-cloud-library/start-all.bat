@echo off
title Library System - Start All

echo ============================================
echo   Campus Library Management System
echo   One-Click Startup
echo ============================================
echo.

set "BASE=C:\Users\46296\Desktop\library"

:: Step 1: Start Nacos
echo [1/8] Starting Nacos Server...
start "Nacos-Server" /D "%BASE%\nacos-server-3.2.0\nacos\bin" cmd /c "startup.cmd -m standalone"
echo   Waiting 15s for Nacos...
timeout /t 15 /nobreak >nul

:: Step 2: Check RabbitMQ
echo [2/8] Checking RabbitMQ...
netstat -ano | findstr ":5672" >nul 2>&1
if %errorlevel% equ 0 (
    echo   RabbitMQ is running
) else (
    echo   WARNING: RabbitMQ not detected on port 5672
)

:: Step 3: Start Sentinel Dashboard
echo [3/8] Starting Sentinel Dashboard...
start "Sentinel-Dashboard" cmd /c "java -jar %BASE%\sentinel\sentinel\sentinel-dashboard-1.8.9.jar --server.port=8090"
echo   Waiting 8s for Sentinel...
timeout /t 8 /nobreak >nul

:: Step 4: Build project
echo [4/8] Building project...
cd /d "%BASE%\library-parent"
call mvn clean install -DskipTests
if %errorlevel% neq 0 (
    echo   BUILD FAILED! Check errors above.
    pause
    exit /b 1
)
echo   Build success

:: Step 5: Start user-service
echo [5/8] Starting user-service (8081)...
start "user-service:8081" cmd /c "cd /d %BASE%\user-service && title user-service:8081 && mvn spring-boot:run"
timeout /t 12 /nobreak >nul

:: Step 6: Start book-service (2 instances)
echo [6/8] Starting book-service instance 1 (8082)...
start "book-service:8082" cmd /c "cd /d %BASE%\book-service && title book-service:8082 && mvn spring-boot:run"

echo   Starting book-service instance 2 (8083) - LB demo...
start "book-service:8083" cmd /c "cd /d %BASE%\book-service && title book-service:8083 && mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8083 -q"
timeout /t 10 /nobreak >nul

:: Step 7: Start borrow + notice
echo [7/8] Starting borrow-service (8084)...
start "borrow-service:8084" cmd /c "cd /d %BASE%\borrow-service && title borrow-service:8084 && mvn spring-boot:run"

echo   Starting notice-service (8085)...
start "notice-service:8085" cmd /c "cd /d %BASE%\notice-service && title notice-service:8085 && mvn spring-boot:run"
timeout /t 8 /nobreak >nul

:: Step 8: Start gateway
echo [8/8] Starting gateway-service (8086)...
start "gateway-service:8086" cmd /c "cd /d %BASE%\gateway-service && title gateway-service:8086 && mvn spring-boot:run"
timeout /t 8 /nobreak >nul

:: Start frontend
echo.
echo Starting frontend (Vite :5173)...
start "library-frontend" cmd /c "cd /d %BASE%\library-frontend && title library-frontend && npm run dev"

echo.
echo ============================================
echo   ALL SERVICES STARTED!
echo ============================================
echo.
echo   Frontend:      http://localhost:5173
echo   API Gateway:   http://localhost:8086
echo   Nacos Console: http://localhost:8848/nacos
echo   Sentinel:      http://localhost:8090
echo   Zipkin:        http://localhost:9411
echo   RabbitMQ Mgmt: http://localhost:15672
echo.
echo   Test accounts:
echo     admin    / admin123  (ADMIN)
echo     student1 / 123456    (STUDENT)
echo.
echo   Run stop-all.bat to stop all services.
echo ============================================
pause
