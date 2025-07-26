# AI问答系统 - 部署构建文档

## 📋 项目概述

AI问答系统是一个基于人工智能的在线题库管理系统，支持从课程资料中自动生成高质量选择题。系统采用前后端分离架构，提供学生、教师、管理员三种角色的完整功能。

### 🏗️ 技术架构

- **后端**: Spring Boot 3.5.0 + MyBatis-Plus + MySQL
- **前端**: Vue 3 + Vite + Element Plus + TypeScript
- **AI服务**: OpenRouter API (OpenAI兼容)
- **数据库**: MySQL 8.0+

### 🎯 功能特性

- **学生端**: 选课申请、在线答题、成绩查看、课程反馈
- **教师端**: 课程管理、文件上传、AI题目生成、学生管理、反馈查看
- **管理员端**: 用户管理、课程管理、选课审批

## 🛠️ 环境要求

### 基础环境
- **操作系统**: Windows 10/11, Linux, macOS
- **Java**: JDK 17+ (推荐 JDK 21)
- **Node.js**: v18.x+ (推荐 v20.x)
- **Maven**: 3.6+
- **MySQL**: 8.0+
- **内存**: 最少 4GB，推荐 8GB+

### 开发工具推荐
- **后端**: IntelliJ IDEA 2023.1+
- **前端**: VS Code / WebStorm
- **数据库**: MySQL Workbench / Navicat

## 📦 项目结构

```
AI-quiz/
├── AIquiz/                    # 后端项目 (Spring Boot)
│   ├── src/main/java/        # Java 源代码
│   ├── src/main/resources/   # 配置文件
│   └── pom.xml              # Maven 配置
├── aipuiz-frontend/          # 前端项目 (Vue 3)
│   ├── src/                 # Vue 源代码
│   ├── public/              # 静态资源
│   └── package.json         # NPM 配置
├── sql/                     # 数据库脚本
│   └── schema.sql          # 数据库表结构
└── docs/                    # 项目文档
```

## 🚀 快速开始

### 1. 克隆项目

```bash
git clone <repository-url>
cd AI-quiz
```

### 2. 数据库配置

#### 2.1 创建数据库
```sql
CREATE DATABASE aiquiz CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 2.2 执行数据库脚本
```bash
mysql -u root -p aiquiz < sql/schema.sql
```

#### 2.3 配置数据库连接
编辑 `AIquiz/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/aiquiz?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### 3. AI服务配置

#### 3.1 创建配置文件
在 `AIquiz/src/main/resources/` 目录下创建 `config.properties`:

```properties
# OpenRouter API 配置
api.key=your_openrouter_api_key
```

#### 3.2 获取 API Key
1. 访问 [OpenRouter](https://openrouter.ai/)
2. 注册账号并获取 API Key
3. 将 API Key 填入 `config.properties`

### 4. 后端部署

#### 4.1 安装依赖
```bash
cd AIquiz
mvn clean install
```

#### 4.2 启动后端服务
```bash
# 开发环境
mvn spring-boot:run

# 或打包后运行
mvn clean package
java -jar target/AIquiz-0.0.1-SNAPSHOT.jar
```

后端服务将在 `http://localhost:8080` 启动

### 5. 前端部署

#### 5.1 安装依赖
```bash
cd aipuiz-frontend
npm install
```

#### 5.2 配置代理
编辑 `aipuiz-frontend/vite.config.ts`，确保后端地址正确:

```typescript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080', // 后端服务地址
      changeOrigin: true,
      rewrite: (path) => path.replace(/^\/api/, ''),
    },
  },
},
```

#### 5.3 启动前端服务
```bash
# 开发环境
npm run dev

# 构建生产版本
npm run build
```

前端服务将在 `http://localhost:3000` 启动

## 🔧 详细配置

### 后端配置详解

#### application.yml 完整配置
```yaml
spring:
  servlet:
    multipart:
      max-file-size: 50MB
      max-request-size: 50MB
  application:
    name: AIquiz
  datasource:
    url: jdbc:mysql://localhost:3306/aiquiz?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver

mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
    map-underscore-to-camel-case: false

server:
  servlet:
    session:
      timeout: 60m
  port: 8080
```

#### 重要配置说明
- **文件上传限制**: 最大50MB，支持PDF、PPT等格式
- **数据库连接**: 使用UTF8编码，支持中文
- **会话超时**: 60分钟
- **MyBatis日志**: 开发环境开启SQL日志

### 前端配置详解

#### Vite 配置
```typescript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''),
      },
    },
  },
})
```

#### TypeScript 配置
```json
{
  "compilerOptions": {
    "target": "esnext",
    "module": "esnext",
    "strict": true,
    "moduleResolution": "node",
    "baseUrl": ".",
    "paths": {
      "@/*": ["src/*"]
    }
  }
}
```

## 🐳 Docker 部署

### 后端 Dockerfile
```dockerfile
FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/AIquiz-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
```

### 前端 Dockerfile
```dockerfile
FROM node:20-alpine as build

WORKDIR /app
COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80
```

### Docker Compose
```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: your_password
      MYSQL_DATABASE: aiquiz
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./sql/schema.sql:/docker-entrypoint-initdb.d/schema.sql

  backend:
    build: ./AIquiz
    ports:
      - "8080:8080"
    depends_on:
      - mysql
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/aiquiz

  frontend:
    build: ./aipuiz-frontend
    ports:
      - "80:80"
    depends_on:
      - backend

volumes:
  mysql_data:
```

## 🔒 安全配置

### 1. 数据库安全
```sql
-- 创建专用数据库用户
CREATE USER 'aiquiz_user'@'localhost' IDENTIFIED BY 'strong_password';
GRANT ALL PRIVILEGES ON aiquiz.* TO 'aiquiz_user'@'localhost';
FLUSH PRIVILEGES;
```

### 2. API 密钥安全
- 将 `config.properties` 添加到 `.gitignore`
- 使用环境变量管理敏感信息
- 定期轮换 API 密钥

### 3. 跨域配置
后端已配置 CORS，支持前端跨域访问

## 📊 性能优化

### 后端优化
1. **数据库连接池**: 使用 HikariCP
2. **缓存**: 集成 Redis 缓存
3. **异步处理**: 大文件上传异步处理
4. **JVM 调优**: 根据服务器内存调整堆大小

### 前端优化
1. **代码分割**: 路由懒加载
2. **静态资源**: CDN 加速
3. **缓存策略**: 浏览器缓存配置
4. **压缩**: Gzip 压缩

## 🐛 故障排除

### 常见问题

#### 1. 后端启动失败
```bash
# 检查 Java 版本
java -version

# 检查 Maven 版本
mvn -version

# 清理并重新构建
mvn clean install
```

#### 2. 数据库连接失败
```bash
# 检查 MySQL 服务状态
systemctl status mysql

# 检查数据库连接
mysql -u root -p -h localhost
```

#### 3. 前端构建失败
```bash
# 清理 node_modules
rm -rf node_modules package-lock.json
npm install

# 检查 Node.js 版本
node -v
```

#### 4. API 调用失败
- 检查后端服务是否启动
- 检查代理配置是否正确
- 检查网络连接

### 日志查看
```bash
# 后端日志
tail -f logs/application.log

# 前端开发日志
npm run dev
```

## 📈 监控和维护

### 1. 应用监控
- 集成 Spring Boot Actuator
- 配置健康检查端点
- 监控应用性能指标

### 2. 日志管理
- 配置日志轮转
- 设置日志级别
- 集成 ELK 日志分析

### 3. 备份策略
- 数据库定期备份
- 配置文件备份
- 代码版本管理

## 🔄 更新部署

### 1. 代码更新
```bash
# 拉取最新代码
git pull origin main

# 重新构建后端
cd AIquiz
mvn clean package

# 重新构建前端
cd ../aipuiz-frontend
npm run build
```

### 2. 数据库迁移
- 备份现有数据
- 执行数据库迁移脚本
- 验证数据完整性

### 3. 服务重启
```bash
# 停止服务
pkill -f "java -jar"
pkill -f "npm run dev"

# 启动服务
java -jar target/AIquiz-0.0.1-SNAPSHOT.jar
npm run dev
```

## 📞 技术支持

### 联系方式
- **邮箱**: 1804240072@qq.com
- **项目地址**: [GitHub Repository]

### 文档资源
- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
- [Vue 3 官方文档](https://vuejs.org/)
- [Element Plus 组件库](https://element-plus.org/)

---

**注意**: 部署前请确保所有配置正确，特别是数据库连接和API密钥配置。如遇问题，请查看日志文件或联系技术支持。