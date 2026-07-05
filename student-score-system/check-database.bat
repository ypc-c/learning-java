@echo off
echo ====================================
echo 数据库检查脚本
echo ====================================
echo.

echo 正在检查 MySQL 连接...
echo 请输入 MySQL root 密码（默认：root）
echo.

mysql -u root -p -e "SELECT VERSION();"
if errorlevel 1 (
    echo.
    echo [错误] 无法连接到 MySQL！
    echo 请检查：
    echo 1. MySQL 服务是否已启动
    echo 2. 用户名密码是否正确
    echo.
    pause
    exit /b 1
)

echo.
echo MySQL 连接成功！
echo.

echo 正在检查数据库是否存在...
mysql -u root -p -e "SHOW DATABASES LIKE 'student_score_system';"

echo.
echo 如果数据库不存在，请执行以下命令导入：
echo mysql -u root -p ^< database\schema.sql
echo.

pause
