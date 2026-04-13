---
name: jdbc-repository-change
description: Implement or modify JDBC repositories and related persistence code in a safe, layered, and project-consistent way.
---

# jdbc-repository-change

用于在 `persistence:jdbc` 中实现或修改 JDBC 持久层代码。

## 适用场景

- 新增 JDBC repository
- 修改 JDBC repository
- 新增 SQL / 查询实现
- 修改 entity / row mapping / converter
- Embedded / app 侧接入本地 JDBC 能力

## 默认约束

- 仅承载持久化职责
- 不放业务编排逻辑
- 与现有 Spring Data JDBC 或项目既有 JDBC 风格保持一致
- 不把 JDBC 调用直接暴露到 WebFlux event loop
- 优先最小改动

## 实施步骤

### 1. 扫描现状

先检查：

- 现有 `persistence:jdbc` 包结构
- 现有 repository 风格
- entity / converter / mapper 组织方式
- SQL 脚本位置
- embedded / app 对该持久层的引用方式

### 2. 明确边界

职责建议：

- repository：查询、保存、更新、删除
- mapper / converter：数据结构转换
- service：业务编排，不放在 repository 层
- schema：数据库初始化或演进脚本

### 3. 设计数据访问方式

要求：

- 参数化查询
- 不拼接高风险 SQL
- 命名、返回模型、异常风格与现有项目一致
- 不引入 ORM

### 4. 检查调用方影响

如改动以下内容，必须评估影响：

- entity 字段
- repository 方法签名
- converter 行为
- schema 结构

要检查：

- app
- embedded
- 相关 SQL 脚本
- 相关调用 service

### 5. 补充测试

至少覆盖：

- 正常路径
- 边界或异常路径
- 查询映射正确性
- 空结果、重复键、非法输入等情形

## 输出要求

最终输出必须说明：

- repository 改动点
- schema / entity / mapper 是否有变更
- 调用方影响
- 参数化与安全性处理
- 验证命令与结果

## 禁止事项

- 不要把业务编排塞进 repository
- 不要把 JDBC repository 描述成 R2DBC repository
- 不要引入 ORM 框架
- 不要无理由修改跨模块公共模型