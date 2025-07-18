# AI-quiz 智能题目生成与管理系统

AI-quiz 是一个基于人工智能的在线题库管理系统，支持从课程资料中自动生成高质量选择题，并支持题目评估、优化和用户测试。

## 📦 项目结构
ai-quiz/
├── AIquiz/ # 后端（Spring Boot）
├── aiquizs-frontend/ # 前端（Vue 3 + Vite）
└── docs/ # 项目文档
- 操作系统：Windows 
- Java：JDK 21+
- Node.js：v18.x+
- Maven：3.6+
- 数据库：MySQL 8+
- IDE 推荐：
    - IntelliJ IDEA (后端)
    - VS Code / WebStorm (前端)
- 确保你已正确安装 JDK 和 Maven。并在后端项目导入时更新依赖

## 2. 配置数据库

     创建数据库（如：aiquiz）并执行 sql/schema.sql脚本导入表结构。
修改 application.yml 数据库配置：

yml文件
```yml
spring:
datasource:
url: jdbc:mysql://localhost:3306/aiquiz
username: root
password: your_password
```

### 3.配置OpenAI API密钥
需要自己创建
-请在AIquiz/main/resources/config.properties中配置OpenAI API密钥：
api.key=密钥

```properties
## 🌐 前端配置（Vue 3）
1. 安装依赖
   cd aiquizs-frontend
   npm install前端配置（Vue 3）
```
## 如需帮助请联系作者1804240072@qq.com
