# AI 小说转剧本工具（Novel-to-Script）

> 七牛云训练营 72 小时开发项目：基于 Spring AI + 七牛云 DeepSeek-v4-pro 的 AI 辅助剧本创作工具。

将 3 章及以上的小说文本自动解析为结构化 YAML 剧本，输出符合行业习惯的「剧本-人物-场景-节拍」四层结构，让作者一步获得可编辑、可打磨的剧本初稿。

## 功能特性

- 多格式上传：TXT / Markdown / HTML，或直接粘贴文本
- 智能预处理：章节自动分割、HTML 清洗、广告与版权过滤
- AI 转换：人物 / 场景 / 对话 / 动作 / 节拍提取
- 实时预览：Monaco Editor + YAML 语法高亮 + Schema 实时校验
- 多格式导出：YAML / Markdown / 纯文本
- 历史记录：转换历史保存与对比

## 技术栈

| 层级 | 选型 |
| ---- | ---- |
| 后端 | Java 17, Spring Boot 3.2.x, Spring AI 1.0.0-M1, Spring Validation, Lombok |
| AI 服务 | 七牛云 DeepSeek-v4-pro API |
| 缓存 | Redis 6.2 |
| 数据库 | H2（开发环境） |
| 前端 | Vue 3, Vite, Element Plus, Axios, Monaco Editor |
| 部署 | Docker, Docker Compose |

## 项目结构

```
qiniuyun/
├── backend/        # Spring Boot 后端
├── frontend/       # Vue 3 前端
├── schema/         # 剧本 YAML Schema 与设计说明
├── docker-compose.yml
└── README.md
```

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.9+
- Node.js 18+
- Redis 6.2+
- 七牛云 DeepSeek API Key

### 本地启动

1. 克隆仓库
   ```bash
   git clone https://github.com/weixiaobing1/qiniuyun.git
   cd qiniuyun
   ```

2. 配置后端环境变量（也可写入 `backend/src/main/resources/application-local.yml`）
   ```bash
   set QINIU_AI_API_KEY=你的-API-Key
   set QINIU_AI_BASE_URL=https://api.qnaigc.com/v1
   ```

3. 启动 Redis（已有则跳过）
   ```bash
   docker run -d --name redis -p 6379:6379 redis:6.2
   ```

4. 启动后端
   ```bash
   cd backend
   mvn spring-boot:run
   ```

5. 启动前端
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

6. 浏览器打开 http://localhost:5173

### Docker Compose 一键部署

```bash
export QINIU_AI_API_KEY=your-api-key
docker-compose up -d
```

访问 http://localhost:8080

## 剧本 YAML Schema

详见 [schema/schema-design.md](schema/schema-design.md)，模板见 [schema/script-schema.yaml](schema/script-schema.yaml)。

四层结构：**剧本 → 人物 → 场景 → 节拍（beats）**，节拍类型包含 `action / dialogue / narration / transition`。

## 开发计划

详见项目 issue 与 PR 历史。72 小时分三天迭代：

- Day 1：项目初始化 → 文本预处理 → AI 转换引擎核心
- Day 2：YAML 生成 → 前端预览编辑 → 端到端整合
- Day 3：导出 / 历史记录 → 测试 → Docker 部署 → Demo 录制

## License

MIT
