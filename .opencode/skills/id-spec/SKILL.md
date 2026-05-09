---
name: id-spec
description: 在 gw-utils 仓库中修改 UUID、ID 生成器、ID 格式转换、ID 序列化或相关公共 API 时必须加载本技能。
compatibility: opencode
metadata:
  domain: java
  purpose: id-and-uuid-utility-convention
---

# id-spec

## 适用场景

- 修改 `core:uuid`
- 修改 UUID / Long ID 生成器
- 修改 ID 字符串格式化、解析、校验
- 修改涉及 ID 的 Jackson / JSON 序列化工具
- 修改 ID 相关公共 API

## 核心规则

1. ID 工具必须线程安全。
2. ID 生成器必须说明唯一性、有序性、碰撞风险与时钟依赖。
3. 公共 API 的 ID 格式变更必须说明兼容性影响。
4. String UUID 工具默认输出不带 `-` 的 32 位字符串时，不得静默改为 36 位格式。
5. Long ID 工具不得依赖数据库自增语义。
6. 测试必须覆盖并发、边界值、非法输入、格式转换。

## 输出要求

- 改动了哪些 ID / UUID 工具。
- 是否影响公共 API。
- 是否影响序列化格式。
- 是否补充并发或边界测试。
