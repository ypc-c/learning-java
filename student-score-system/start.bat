@echo off
echo ====================================
echo 学生成绩管理系统启动脚本
echo ====================================
echo.

echo 正在检查 Maven...
call mvn -version
if errorlevel 1 (
    echo Maven 未安装或未配置环境变量！
    pause
    exit /b 1
)

echo.
echo 正在启动项目...
echo 请确保 MySQL 数据库已启动并导入了 database/schema.sql
echo.
echo 启动后访问：http://localhost:8080
echo.

call mvnw.cmd spring-boot:run

pause
