---
name: embedded-sqlite-jdbc-bridge
description: Implement or modify SQLite + JDBC persistence in the embedded module while preserving WebFlux and isolating blocking calls correctly.
---

# embedded-sqlite-jdbc-bridge

用于在 `embedded` 模块中实现或修改 SQLite + JDBC 本地轻量持久化能力，并确保与 WebFlux 主链路正确隔离。

## 适用场景

- Embedded 本地配置持久化
- Embedded 本地状态持久化
- Embedded 本地轻量日志索引 / 摘要存储
- SQLite schema 初始化
- `persistence:jdbc` 在 embedded 中的接入与改造
- 从本地 MySQL / 不合适的本地持久化方案迁移到 SQLite + JDBC

## 核心架构原则

Embedded 必须保持：

- WebFlux
- Reactive WebSocket
- 非阻塞主链路

本地持久化使用：

- SQLite
- JDBC
- blocking bridge / 专用 Scheduler 隔离

## 允许用途

SQLite 仅用于：

- 配置信息
- 少量运行状态
- 轻量日志索引 / 摘要
- 必要初始化数据

## 禁止用途

不要将 SQLite 用于：

- 视频流入库
- 音频流入库
- 大量原始日志全文入库
- 高并发主业务数据库
- Edge / Cloud 的同步主库

## 实施步骤

### 1. 扫描 embedded 现状

优先检查：

- `embedded/src/main/java/**/config/**`
- `embedded/src/main/resources/application.properties`
- `embedded/src/main/resources/env/*`
- `embedded/src/main/resources/db/*`
- `persistence/jdbc`
- `README.md`
- 安装与部署脚本

### 2. 识别当前本地持久化边界

明确：

- 当前哪些数据确实需要本地保存
- 数据量是否适合 SQLite
- 是否已有 JDBC 基础设施
- 是否已有 SQLite schema 或 DataSource 配置
- 是否已有 blocking Scheduler

### 3. 设计 JDBC 调用边界

要求：

- JDBC 不得直接运行在 Netty event loop
- JDBC 必须通过显式 blocking bridge 隔离
- repository 只做持久化职责
- service 只做业务编排
- 不把 JDBC 细节泄漏到 controller / WebSocket handler

### 4. SQLite 配置设计

优先保证：

- 数据库文件路径可配置
- 首次启动可初始化 schema
- 配置命名与项目现有风格一致
- 文档与脚本同步更新

### 5. schema 与数据模型

要求：

- 只保留轻量必要字段
- 不存储大对象、媒体内容、超长日志主体
- 结构保持稳定、可维护
- 如已有表结构，优先最小变更

### 6. 验证 WebFlux 主链路未被破坏

必须检查：

- controller / handler / WebSocket handler 是否直接调 JDBC
- service 中是否正确隔离阻塞访问
- 是否保留 WebFlux / Reactive WebSocket 配置
- 是否误引入本地 MySQL / R2DBC 强依赖

## 推荐输出内容

最终说明中应明确：

- SQLite 用于哪些数据
- JDBC 在哪里被隔离
- 为什么该方案符合 embedded 架构
- 哪些地方仍保持 WebFlux 主链路
- 哪些脚本 / 文档已同步

## 推荐验证命令

```bash
export JAVA_HOME=/Users/geewit/.sdkman/candidates/java/25.0.2-graal
export GRADLE_HOME=/Users/geewit/.sdkman/candidates/gradle/current
export PATH="$JAVA_HOME/bin:$GRADLE_HOME/bin:$PATH"

gradle :embedded:compileJava
gradle :embedded:test
bash ./.opencode/shell/verify-embedded.sh
```

## 禁止事项

- 不要把 Embedded 改成 WebMvc
- 不要把 SQLite 作为主业务数据库
- 不要把阻塞 JDBC 放到 event loop
- 不要为 SQLite 引入 ORM
- 不要做与本任务无关的大规模基础设施改造