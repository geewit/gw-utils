# Skill: DDD DDL and Persistence Separation

## 适用场景

当任务涉及：
- 表设计调整
- R2DBC/JDBC 持久化实现重构
- 数据库结构与领域模型解耦

## 强制规则

1. 先分清上下文，再设计表；不要先用一张大表兜所有概念。
2. persistence model 不等于 domain model。
3. repository 接口表达聚合持久化意图，repository implementation 负责数据库细节。
4. 查询模型和命令模型允许分离，必要时不要强求一个对象打天下。
5. 输出数据库改造方案时，必须说清：
   - 复用哪些旧表
   - 新增哪些表
   - 哪些字段兼容保留
   - 哪些索引支撑主查询路径
   - 哪些约束保证一致性

## 针对本项目的提醒

- SQLite/JDBC 只适合 embedded 轻量本地持久化
- MySQL/R2DBC 只适合 server 侧
- 不要把 RTSP 数据面日志塞进 HTTP_REQUEST_LOG
