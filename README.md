# 研学基地科普教具使用责任人绑定变更日志汇总系统

## 项目简介

本系统用于管理研学基地各类科普教具，核心业务为**人员责任人绑定**和**全量变更日志按月汇总**。系统支持教具建档、责任人绑定与更换、变更日志记录、月度汇总等功能。

## 技术栈

- **前端**: Vue 3 + Vite + TypeScript + Element Plus
- **后端**: Spring Boot 3.3 + JDK 17 + Spring Data JPA + Redis
- **数据库**: MySQL 8.0
- **缓存**: Redis 7
- **容器化**: Docker + Docker Compose

## 功能模块

| 模块 | 功能描述 |
|------|---------|
| 科普教具基础建档 | 编号、科普品类、适配研学班级等信息管理 |
| 教职工责任人初始绑定 | 分配教具专属负责人员 |
| 责任人更换登记 | 记录新旧负责人、调整时间、操作人 |
| 按月自动汇总变更日志 | 生成月度变更台账总览 |
| 按教职工反向查询 | 查询名下全部绑定教具 |

## 项目结构

```
qyx-201/
├── .env                    # 环境变量配置
├── docker-compose.yml      # Docker Compose 配置
├── .gitignore              # Git 忽略规则
├── .dockerignore           # Docker 构建忽略规则
├── start.sh                # 启动脚本
├── README.md               # 项目说明文档
├── backend/                # Spring Boot 后端
│   ├── pom.xml             # Maven 依赖配置
│   ├── Dockerfile          # 后端 Docker 构建文件
│   └── src/main/java/com/example/edukit/
│       ├── EduKitApplication.java
│       ├── controller/     # REST API 控制器
│       ├── service/        # 业务逻辑层
│       ├── repository/     # 数据访问层
│       ├── entity/         # JPA 实体类
│       ├── dto/            # 数据传输对象
│       └── config/         # 配置类
└── frontend/               # Vue3 + Vite 前端
    ├── package.json        # npm 依赖配置
    ├── vite.config.ts      # Vite 配置
    ├── Dockerfile          # 前端 Docker 构建文件
    └── src/
        ├── main.ts         # 应用入口
        ├── api/index.ts    # API 服务封装
        ├── types/index.ts  # TypeScript 类型定义
        ├── router/index.ts # 路由配置
        └── pages/          # 页面组件
```

## 端口配置

| 服务 | 端口 |
|------|------|
| 前端 | 8141 |
| 后端 | 8151 |
| MySQL | 3357 |
| Redis | 6430 |

## 快速开始

### 环境要求

- JDK 17+
- Node.js 20+
- Docker & Docker Compose

### 启动方式

#### 方式一：使用启动脚本

```bash
chmod +x start.sh
./start.sh
```

#### 方式二：手动启动

```bash
# 启动所有服务
docker-compose up -d --build

# 查看服务状态
docker-compose ps
```

### 单独编译验证

```bash
# 后端编译（需配置 JAVA_HOME）
cd backend
mvn compile -q

# 前端构建
cd frontend
npm ci
npm run build
```

## API 接口

### 教具管理

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/kits | 获取所有教具 |
| GET | /api/kits/{id} | 获取指定教具 |
| POST | /api/kits | 创建教具 |
| PUT | /api/kits/{id} | 更新教具 |
| DELETE | /api/kits/{id} | 删除教具 |
| GET | /api/kits/category/{category} | 按品类查询 |
| GET | /api/kits/staff/{staffId} | 按责任人查询 |

### 教职工管理

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/staff | 获取所有教职工 |
| GET | /api/staff/{id} | 获取指定教职工 |
| POST | /api/staff | 创建教职工 |
| PUT | /api/staff/{id} | 更新教职工 |
| DELETE | /api/staff/{id} | 删除教职工 |
| GET | /api/staff/search?name=xxx | 按姓名搜索 |

### 变更日志

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /api/change-logs/change | 更换责任人 |
| GET | /api/change-logs | 获取所有变更日志 |
| GET | /api/change-logs/monthly/current | 获取当月汇总 |
| GET | /api/change-logs/monthly?year=2026&month=7 | 获取指定月份汇总 |

## 访问地址

- **前端页面**: http://localhost:8141
- **后端 API**: http://localhost:8151

## 配置说明

### 环境变量

所有端口和密码配置均在 `.env` 文件中：

```
APP_NAME=edu-kit-system
FRONTEND_PORT=8141
BACKEND_PORT=8151
MYSQL_PORT=3357
REDIS_PORT=6430
MYSQL_ROOT_PASSWORD=root123
MYSQL_DATABASE=edu_kit_db
MYSQL_USER=edu_user
MYSQL_PASSWORD=edu_pass123
REDIS_PASSWORD=redis123
SPRING_PROFILES_ACTIVE=docker
```

### Docker 构建缓存

- 前端 Dockerfile：先 COPY package.json/package-lock.json 安装依赖，再 COPY 源码 build
- 后端 Dockerfile：先 COPY pom.xml/settings.xml 下载依赖，再 COPY src 编译

## 中文支持

系统已配置完整的中文支持：

- 数据库连接使用 `characterEncoding=utf8`
- 前端页面设置 `charset=UTF-8`
- MySQL 数据库初始化脚本设置 `utf8mb4` 字符集

## 开发规范

1. 端口必须固定且显式配置，禁止使用默认端口
2. Docker Compose 端口必须绑定 127.0.0.1
3. 首次构建允许下载依赖，后续构建应使用缓存
4. 中文数据必须确保 UTF-8 编码
