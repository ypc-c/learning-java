@echo off
chcp 65001 >nul
echo ====================================
echo 数据库初始化脚本
echo ====================================
echo.

echo 此脚本将创建数据库并导入初始数据
echo 请确保 MySQL 服务已启动
echo.

set /p password=请输入 MySQL root 密码（默认回车使用 root）: 
if "%password%"=="" set password=root

echo.
echo 正在导入数据库...
mysql -u root -p%password% < database\schema.sql

if errorlevel 1 (
    echo.
    echo [错误] 数据库导入失败！
    echo 请检查：
    echo 1. MySQL 服务是否已启动
    echo 2. 密码是否正确
    echo 3. database\schema.sql 文件是否存在
    echo.
    pause
    exit /b 1
)

echo.
echo [成功] 数据库导入成功！
echo.
echo 数据库名称：student_score_system
echo 测试账号已创建：
echo   管理员：admin / admin123
echo   教师：teacher1 / 123456
echo   学生：2024001 / 123456
echo.

echo 请修改 src\main\resources\application.properties 中的数据库密码
echo 当前配置的密码是：root
echo.

pause
