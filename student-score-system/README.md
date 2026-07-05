# 学生成绩管理系统

基于 Spring Boot + MyBatis + MySQL 的学生成绩管理系统

## 功能特性

### 1. 用户模块
- **超级管理员**：账号登录、创建/删除教师账号、个人信息修改
- **教师**：账号登录、个人信息修改、密码重置
- **学生**：账号登录（学号）、个人信息修改、密码找回（手机号）

### 2. 课程管理模块
- **超级管理员**：课程 CRUD、按课程名称模糊查询、按学期精准查询
- **教师**：查询授课课程、查看课程学生列表

### 3. 成绩管理模块（教师）
- 成绩录入：单个录入或批量导入 Excel
- 成绩计算：总成绩 = 平时30% + 期中20% + 期末50%
- 成绩修改：自动重新计算总成绩
- 成绩查询：按学生姓名/学号模糊查询、按总成绩区间筛选
- 成绩统计：平均分、最高分、最低分、各等级人数及占比

### 4. 成绩查询模块（学生）
- 个人成绩查询：查看所有课程成绩
- 成绩排序：按总成绩或学分排序
- 绩点计算：自动计算课程绩点和总绩点

## 技术栈

### 后端
- Spring Boot 4.0.1
- MyBatis 3.0.3
- MySQL 8.0+
- JWT 认证
- Apache POI（Excel 处理）
- Lombok

### 前端
- HTML5 + CSS3 + JavaScript
- 原生 Fetch API

## 快速开始

### 1. 环境要求
- JDK 17+
- Maven 3.6+
- MySQL 8.0+

### 2. 数据库配置

```bash
# 创建数据库并导入数据
mysql -u root -p < database/schema.sql
```

或手动执行：
1. 创建数据库 `student_score_system`
2. 执行 `database/schema.sql` 中的 SQL 语句

### 3. 修改配置

编辑 `src/main/resources/application.properties`：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/student_score_system?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=你的密码
```

### 4. 启动项目

```bash
# 使用 Maven 启动
mvn spring-boot:run

# 或者使用 Maven Wrapper（Windows）
mvnw.cmd spring-boot:run
```

### 5. 访问系统

打开浏览器访问：http://localhost:8080

## 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 超级管理员 | admin | admin123 |
| 教师 | teacher1 | 123456 |
| 教师 | teacher2 | 123456 |
| 学生 | 2024001 | 123456 |
| 学生 | 2024002 | 123456 |
| 学生 | 2024003 | 123456 |

## 项目结构

```
student-score-system/
├── database/                      # 数据库脚本
│   ├── schema.sql                # 数据库结构和初始数据
│   └── excel_template.md         # Excel 导入模板说明
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── org/score/studentscoresystem/
│   │   │       ├── common/       # 通用类
│   │   │       │   └── Result.java
│   │   │       ├── controller/   # 控制器
│   │   │       │   ├── AuthController.java
│   │   │       │   ├── UserController.java
│   │   │       │   ├── CourseController.java
│   │   │       │   └── ScoreController.java
│   │   │       ├── dto/          # 数据传输对象
│   │   │       │   ├── LoginDTO.java
│   │   │       │   └── ScoreStatisticsDTO.java
│   │   │       ├── entity/       # 实体类
│   │   │       │   ├── User.java
│   │   │       │   ├── Course.java
│   │   │       │   ├── Score.java
│   │   │       │   └── CourseStudent.java
│   │   │       ├── mapper/       # MyBatis Mapper
│   │   │       │   ├── UserMapper.java
│   │   │       │   ├── CourseMapper.java
│   │   │       │   ├── ScoreMapper.java
│   │   │       │   └── CourseStudentMapper.java
│   │   │       ├── service/      # 服务层
│   │   │       │   ├── UserService.java
│   │   │       │   ├── CourseService.java
│   │   │       │   └── ScoreService.java
│   │   │       ├── util/         # 工具类
│   │   │       │   ├── JwtUtil.java
│   │   │       │   └── ScoreCalculator.java
│   │   │       └── StudentScoreSystemApplication.java
│   │   └── resources/
│   │       ├── mapper/           # MyBatis XML
│   │       │   ├── UserMapper.xml
│   │       │   ├── CourseMapper.xml
│   │       │   ├── ScoreMapper.xml
│   │       │   └── CourseStudentMapper.xml
│   │       ├── static/           # 前端页面
│   │       │   ├── index.html
│   │       │   ├── login.html
│   │       │   ├── admin.html
│   │       │   ├── teacher.html
│   │       │   ├── student.html
│   │       │   └── common.js
│   │       └── application.properties
│   └── test/
└── pom.xml
```

## API 接口

### 认证接口
- POST `/api/auth/login` - 用户登录
- POST `/api/auth/reset-password` - 重置密码
- POST `/api/auth/change-password` - 修改密码

### 用户接口
- GET `/api/user/info` - 获取用户信息
- PUT `/api/user/update` - 更新用户信息
- GET `/api/user/teachers` - 获取教师列表
- GET `/api/user/students` - 获取学生列表
- GET `/api/user/students/course/{courseId}` - 获取课程学生
- POST `/api/user/create` - 创建用户
- DELETE `/api/user/{id}` - 删除用户

### 课程接口
- GET `/api/course/list` - 获取所有课程
- GET `/api/course/my-courses` - 获取我的课程
- GET `/api/course/{id}` - 获取课程详情
- GET `/api/course/search/name` - 按名称搜索
- GET `/api/course/search/semester` - 按学期搜索
- POST `/api/course/create` - 创建课程
- PUT `/api/course/update` - 更新课程
- DELETE `/api/course/{id}` - 删除课程

### 成绩接口
- GET `/api/score/my-scores` - 获取我的成绩
- GET `/api/score/course/{courseId}` - 获取课程成绩
- GET `/api/score/search` - 搜索成绩
- POST `/api/score/save` - 保存成绩
- PUT `/api/score/update` - 更新成绩
- POST `/api/score/import` - 批量导入成绩
- GET `/api/score/statistics/{courseId}` - 成绩统计

## 成绩计算规则

### 总成绩计算
```
总成绩 = 平时成绩 × 30% + 期中成绩 × 20% + 期末成绩 × 50%
```

### 等级划分
- 优秀：≥ 90分
- 良好：≥ 80分
- 及格：≥ 60分
- 不及格：< 60分

### 绩点计算
- 优秀：4.0
- 良好：3.0
- 及格：2.0
- 不及格：0

### 总绩点计算
```
总绩点 = Σ(课程绩点 × 课程学分) / Σ(课程学分)
```

## Excel 导入格式

参考 `database/excel_template.md` 文件

列格式：学号 | 平时成绩 | 期中成绩 | 期末成绩

## 注意事项

1. 首次运行前请确保 MySQL 服务已启动
2. 数据库密码需要根据实际情况修改
3. JWT 密钥建议在生产环境中修改
4. Excel 导入文件大小限制为 10MB
5. 所有密码在生产环境应使用加密存储（当前为明文存储，仅供演示）

## 开发建议

### 生产环境优化
1. 密码加密：使用 BCrypt 或其他加密算法
2. 日志管理：集成 Logback 或 Log4j2
3. 异常处理：统一异常处理机制
4. 接口文档：集成 Swagger/OpenAPI
5. 数据校验：完善参数校验
6. 事务管理：添加事务注解
7. 缓存优化：Redis 缓存热点数据
8. 安全加固：HTTPS、CORS 配置、SQL 注入防护

## 许可证

MIT License
