---
name: r2dbc-repository-change
description: Implement or modify R2DBC persistence code safely for reactive server modules.
---

# r2dbc-repository-change

用于在 `persistence:r2dbc` 中实现或修改 R2DBC 持久层代码。

## 适用场景

- server/cloud 或 server/edge 的 repository 改动
- 新增 R2DBC entity / repository / converter
- 修改 MySQL 8 + R2DBC 查询
- reactive 数据访问优化

## 默认约束

- 保持 reactive 数据访问
- 不引入阻塞调用
- 不引入 ORM
- repository 层只承载持久化职责
- 优先最小改动

## 实施步骤

### 1. 扫描现状

检查：

- `persistence:r2dbc` 的包结构
- entity / converter / repository 现有实现
- server/cloud、server/edge 的调用方式
- SQL 与 schema 位置
- 是否已有自定义 repository 实现

### 2. 明确边界

- repository：只做数据访问
- service：业务编排
- converter / mapper：模型转换
- 不把业务逻辑下沉到 repository

### 3. 保持 reactive 风格

要求：

- 返回 `Mono` / `Flux`
- 不使用 `block()`
- 不混入同步 JDBC 风格代码
- 不在 repository 中做阻塞桥接

### 4. 查询设计

要求：

- 参数化查询
- 避免字符串拼接导致注入风险
- 命名与现有仓库风格一致
- 返回结构明确
- 对空结果、分页、排序行为保持清晰

### 5. 检查影响面

如改动以下内容，必须检查：

- entity 字段
- converter
- repository 接口签名
- SQL 结构
- server/cloud 与 server/edge 调用逻辑

### 6. 补充测试

至少覆盖：

- 正常路径
- 空结果或异常路径
- 关键映射行为
- 关键查询过滤条件

## 输出要求

最终输出必须包含：

- 改动的 repository / entity / converter
- 是否保持 reactive 端到端
- 受影响模块
- 安全性处理
- 验证命令与结果

## 禁止事项

- 不要把阻塞 JDBC 混进 `persistence:r2dbc`
- 不要用 `block()` 获取结果
- 不要把业务编排逻辑写进 repository
- 不要修改无关 server 模块