# 校园图书管理系统

基于 Spring Cloud 微服务架构的校园图书管理系统，提供用户管理、图书管理、借阅管理、通知管理等功能。

## 项目架构

### 系统架构图

```
┌─────────────────┐
│  Frontend (Vue3)│
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Gateway        │  ← 路由、鉴权、限流、CORS
│  (8086)         │
└────────┬────────┘
         │
    ┌────┴─────────────────────────┐
    │              │               │
    ▼              ▼               ▼
┌─────────┐   ┌─────────┐    ┌─────────┐
│ user    │   │ book    │    │ borrow  │
│ service │   │ service │    │ service │
│ (8081)  │   │ (8082)  │    │ (8083)  │
└────┬────┘   └────┬────┘    └────┬────┘
     │             │              │
     └─────────────┼──────────────┘
                   │
                   ▼
              ┌─────────┐
              │ notice  │
              │ service │
              │ (8084)  │
              └────┬────┘
                   │
    ┌──────────────┼──────────────┐
    │              │              │
    ▼              ▼              ▼
┌─────────┐   ┌─────────┐    ┌─────────┐
│  Nacos  │   │ RabbitMQ│    │  MySQL  │
│(8848)   │   │ (5672)  │    │ (3306)  │
└─────────┘   └─────────┘    └─────────┘
```

### 微服务模块

| 模块 | 端口 | 说明 |
|------|------|------|
| [library-parent](library-parent/pom.xml) | - | 父 POM，统一依赖版本管理 |
| [library-common](library-common/pom.xml) | - | 公共模块：实体类、DTO、VO、工具类 |
| [user-service](user-service/pom.xml) | 8081 | 用户服务：注册、登录、用户管理 |
| [book-service](book-service/pom.xml) | 8082 | 图书服务：图书 CRUD、库存管理 |
| [borrow-service](borrow-service/pom.xml) | 8083 | 借阅服务：借阅、归还、逾期检查 |
| [notice-service](notice-service/pom.xml) | 8084 | 通知服务：公告、逾期通知 |
| [gateway-service](gateway-service/pom.xml) | 8086 | API 网关：路由、鉴权、限流 |
| [library-frontend](library-frontend/package.json) | 5173 | 前端：Vue3 + Element Plus |

## 技术栈

### 后端技术栈

- **框架**：Spring Boot 2.7.18 + Spring Cloud 2021.0.9
- **微服务**：Spring Cloud Alibaba 2021.0.6.1
- **注册/配置中心**：Nacos 3.2.0
- **服务网关**：Spring Cloud Gateway
- **服务调用**：OpenFeign + Spring Cloud LoadBalancer
- **限流熔断**：Alibaba Sentinel
- **消息队列**：RabbitMQ + Spring Cloud Stream
- **链路追踪**：Spring Cloud Sleuth + Zipkin
- **持久层**：MyBatis-Plus 3.5.3.1
- **数据库**：MySQL 8.0
- **认证**：JWT (jjwt 0.9.1)
- **工具库**：Hutool 5.8.23 + Lombok
- **Java 版本**：JDK 17

### 前端技术栈

- **框架**：Vue 3.4
- **构建工具**：Vite 5.4
- **语言**：TypeScript 5.4
- **UI 组件库**：Element Plus 2.7
- **路由**：Vue Router 4.3
- **状态管理**：Pinia 2.1
- **HTTP 客户端**：Axios 1.7
- **图标库**：@element-plus/icons-vue 2.3

## 项目结构

```
library/
├── library-parent/          # 父 POM
├── library-common/          # 公共模块
│   └── src/main/java/com/library/common/
│       ├── config/          # 配置类（MyBatisPlusConfig）
│       ├── constant/        # 常量
│       ├── dto/             # 数据传输对象
│       ├── entity/          # 实体类
│       ├── utils/           # 工具类（JwtUtil, ResultUtil）
│       └── vo/              # 视图对象
├── user-service/            # 用户服务
│   └── src/main/java/com/library/user/
│       ├── controller/      # 控制器
│       ├── service/         # 业务逻辑
│       ├── mapper/          # 数据访问
│       └── config/          # Sentinel 配置
├── book-service/            # 图书服务
│   └── src/main/java/com/library/book/
│       ├── controller/      # 控制器
│       ├── service/         # 业务逻辑
│       └── mapper/          # 数据访问
├── borrow-service/          # 借阅服务
│   └── src/main/java/com/library/borrow/
│       ├── controller/      # 控制器
│       ├── service/         # 业务逻辑
│       ├── mapper/          # 数据访问
│       ├── feign/           # Feign 客户端
│       └── message/         # 消息生产者
├── notice-service/          # 通知服务
│   └── src/main/java/com/library/notice/
│       ├── controller/      # 控制器
│       ├── service/         # 业务逻辑
│       ├── mapper/          # 数据访问
│       └── message/         # 消息消费者
├── gateway-service/         # 网关服务
│   └── src/main/java/com/library/gateway/
│       ├── config/          # 配置类（CORS、Sentinel）
│       └── filter/          # 全局过滤器（鉴权）
├── library-frontend/        # 前端项目
│   └── src/
│       ├── api/             # API 接口
│       ├── router/          # 路由配置
│       ├── stores/          # Pinia 状态
│       ├── utils/           # 工具函数
│       ├── views/           # 页面组件
│       │   ├── admin/       # 管理员页面
│       │   ├── book/        # 图书页面
│       │   ├── borrow/      # 借阅页面
│       │   ├── layout/      # 布局组件
│       │   ├── login/       # 登录注册
│       │   └── notice/      # 通知页面
│       ├── App.vue
│       └── main.ts
└── nacos-server-3.2.0/      # Nacos 服务（内置）
```

## 核心功能

### 用户服务 (user-service)

- 用户注册与登录（JWT 认证）
- 用户信息管理
- 管理员用户列表
- Sentinel 限流保护登录/注册接口

### 图书服务 (book-service)

- 图书分页查询（支持关键词、分类搜索）
- 图书详情查看
- 图书 CRUD（管理员）
- 库存管理（内部 API，供借阅服务调用）

### 借阅服务 (borrow-service)

- 图书借阅
- 图书归还
- 我的借阅记录
- 全部借阅记录（管理员）
- 逾期检查与处理
- 逾期消息发送（RabbitMQ）
- Sentinel 限流保护借阅接口
- Feign 调用图书服务和用户服务

### 通知服务 (notice-service)

- 公告列表（分页）
- 我的通知（个人 + 公共）
- 通知详情
- 通知 CRUD（管理员）
- 逾期通知消费（RabbitMQ）

### 网关服务 (gateway-service)

- 动态路由（基于 Nacos 服务发现）
- 全局 JWT 鉴权过滤器
- 管理员权限校验
- CORS 跨域配置
- Sentinel 网关限流
- 请求头传递用户信息

### 前端功能

- 用户登录/注册
- 图书浏览与搜索
- 图书详情
- 我的借阅
- 通知公告
- 管理员后台：
  - 用户管理
  - 图书管理
  - 借阅管理
  - 通知管理

## 数据库设计

### t_user - 用户表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| username | VARCHAR | 用户名 |
| password | VARCHAR | 密码 |
| real_name | VARCHAR | 真实姓名 |
| role | VARCHAR | 角色（USER/ADMIN） |
| phone | VARCHAR | 手机号 |
| email | VARCHAR | 邮箱 |
| status | INT | 状态 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### t_book - 图书表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| isbn | VARCHAR | ISBN 编号 |
| title | VARCHAR | 书名 |
| author | VARCHAR | 作者 |
| publisher | VARCHAR | 出版社 |
| category | VARCHAR | 分类 |
| description | TEXT | 描述 |
| cover_url | VARCHAR | 封面图片 |
| total_copies | INT | 总册数 |
| available_copies | INT | 可借册数 |
| status | INT | 状态 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### t_borrow_record - 借阅记录表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| user_id | BIGINT | 用户 ID |
| book_id | BIGINT | 图书 ID |
| borrow_time | DATETIME | 借阅时间 |
| due_time | DATETIME | 应还时间 |
| return_time | DATETIME | 实际归还时间 |
| status | VARCHAR | 状态（BORROWED/RETURNED/OVERDUE） |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### t_notice - 通知表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| title | VARCHAR | 标题 |
| content | TEXT | 内容 |
| type | VARCHAR | 类型（PUBLIC/PERSONAL/OVERDUE） |
| target_user_id | BIGINT | 目标用户 ID |
| is_read | INT | 是否已读 |
| create_time | DATETIME | 创建时间 |

## 环境准备

### 必需软件

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- RabbitMQ 3.x
- Nacos 3.2.0（项目内置）
- Node.js 16+
- npm 或 pnpm

## 快速开始

### 1. 启动 Nacos

项目已内置 Nacos 服务器，直接启动：

```bash
cd nacos-server-3.2.0/nacos/bin

# Windows
startup.cmd

# Linux/Mac
sh startup.sh
```

启动后访问：http://localhost:8848/nacos  
默认账号：nacos / nacos

### 2. 准备数据库

创建四个数据库：

```sql
CREATE DATABASE library_user CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE DATABASE library_book CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE DATABASE library_borrow CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE DATABASE library_notice CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

各服务使用独立数据库，表结构由 MyBatis-Plus 实体类对应。

### 3. 启动 RabbitMQ

确保 RabbitMQ 服务已启动，默认端口 5672。

### 4. 启动 Zipkin（可选）

```bash
# 使用 Docker
docker run -d -p 9411:9411 openzipkin/zipkin
```

访问：http://localhost:9411

### 5. 启动 Sentinel 控制台（可选）

下载 Sentinel Dashboard jar 包后启动：

```bash
java -jar sentinel-dashboard-1.8.6.jar
```

访问：http://localhost:8090  
默认账号：sentinel / sentinel

### 6. 启动后端服务

进入 `library-parent` 目录，执行 Maven 构建：

```bash
cd library-parent
mvn clean install -DskipTests
```

按顺序启动各服务：

1. user-service (8081)
2. book-service (8082)
3. borrow-service (8083)
4. notice-service (8084)
5. gateway-service (8086)

### 7. 启动前端

```bash
cd library-frontend
npm install
npm run dev
```

访问：http://localhost:5173

## API 接口

### 用户接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | /user/login | 用户登录 | 公开 |
| POST | /user/register | 用户注册 | 公开 |
| GET | /user/info | 获取当前用户信息 | 用户 |
| GET | /user/list | 用户列表 | 管理员 |
| GET | /user/{id} | 根据 ID 获取用户 | 内部 |

### 图书接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /book/list | 图书列表（分页） | 用户 |
| GET | /book/{id} | 图书详情 | 用户 |
| POST | /book | 新增图书 | 管理员 |
| PUT | /book/{id} | 更新图书 | 管理员 |
| DELETE | /book/{id} | 删除图书 | 管理员 |
| PUT | /book/{id}/stock | 更新库存 | 内部 |

### 借阅接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | /borrow | 借阅图书 | 用户 |
| POST | /borrow/return/{recordId} | 归还图书 | 用户 |
| GET | /borrow/my | 我的借阅记录 | 用户 |
| GET | /borrow/list | 全部借阅记录 | 管理员 |
| POST | /borrow/overdue-check | 触发逾期检查 | 测试 |

### 通知接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /notice/list | 公告列表（分页） | 用户 |
| GET | /notice/my | 我的通知 | 用户 |
| GET | /notice/{id} | 通知详情 | 用户 |
| POST | /notice | 创建通知 | 管理员 |
| DELETE | /notice/{id} | 删除通知 | 管理员 |

## 配置说明

### 网关配置

网关配置文件位于 [gateway-service/src/main/resources/application.yml](gateway-service/src/main/resources/application.yml)

- 服务端口：8086
- 路由规则：基于路径转发到对应服务
- JWT 密钥：`jwt.secret`
- Sentinel 限流：默认响应 429

### 服务配置

各服务配置文件位于各自的 `src/main/resources/` 目录下：

- `bootstrap.yml`：Nacos 配置
- `application.yml`：应用配置

数据库连接配置（默认）：
- URL：`jdbc:mysql://127.0.0.1:3306/library_xxx`
- 用户名：root
- 密码：123456

### 前端配置

前端配置文件位于 [library-frontend/vite.config.ts](library-frontend/vite.config.ts)

- 开发服务器端口：5173
- API 代理：转发到网关 `http://localhost:8086`

## 核心特性

1. **微服务架构**：基于 Spring Cloud + Spring Cloud Alibaba 的完整微服务体系
2. **服务注册与发现**：Nacos 实现服务注册与配置管理
3. **API 网关**：Spring Cloud Gateway 统一入口，支持动态路由
4. **服务间调用**：OpenFeign 声明式调用，集成负载均衡
5. **限流熔断**：Sentinel 实现流量控制和服务降级
6. **消息驱动**：Spring Cloud Stream + RabbitMQ 实现异步消息
7. **链路追踪**：Sleuth + Zipkin 分布式链路追踪
8. **JWT 认证**：网关统一鉴权，支持用户和管理员角色
9. **统一返回**：Result 封装统一响应格式
10. **前后端分离**：Vue3 + Element Plus 现代化前端

## 开发规范

### 后端规范

- 统一使用 [Result](library-common/src/main/java/com/library/common/dto/Result.java) 作为返回值
- 使用 [ResultUtil](library-common/src/main/java/com/library/common/utils/ResultUtil.java) 构建返回结果
- 实体类放在 `library-common` 的 `entity` 包
- DTO 放在 `library-common` 的 `dto` 包
- VO 放在 `library-common` 的 `vo` 包
- 使用 Lombok 简化代码
- 使用 MyBatis-Plus 简化数据访问

### 前端规范

- API 接口定义在 `src/api/` 目录
- 使用 Pinia 进行状态管理
- 使用 Axios 进行 HTTP 请求
- 路由守卫进行权限控制
- 管理员页面通过 `meta.role` 标识

## 注意事项

1. 各服务使用独立数据库，请确保数据库已创建
2. Nacos 默认使用内嵌 Derby 数据库，生产环境建议使用 MySQL
3. JWT 密钥请在生产环境修改
4. 数据库密码请根据实际情况修改
5. Sentinel 配置在首次访问后才会在控制台显示
6. 逾期检查可通过 `/borrow/overdue-check` 接口手动触发测试
