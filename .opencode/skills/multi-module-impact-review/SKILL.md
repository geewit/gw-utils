---
name: multi-module-impact-review
description: Review the impact of changes across a multi-module Java repository before and after implementation.
---

# multi-module-impact-review

用于在多模块 Java 仓库中分析某次改动的影响范围。

## 适用场景

- 修改公共模块
- 修改 `core:*`
- 修改 `persistence:*`
- 修改公共 DTO / constants / config
- 修改接口契约
- 修改 Gradle 依赖关系
- 改动看似局部，但可能影响多个模块

## 目标

1. 识别直接影响模块
2. 识别潜在间接影响模块
3. 识别是否有兼容性风险
4. 指导验证范围

## 分析步骤

### 1. 定位改动模块

先确定改动发生在哪一层：

- app
- embedded
- server/edge
- server/cloud
- core/*
- persistence/*
- scripts / doc

### 2. 识别变更类型

变更属于哪一类：

- 实现细节变更
- API 签名变更
- DTO 字段变更
- config key 变更
- schema 变更
- 依赖关系变更
- 脚本 / 文档变更

### 3. 识别直接调用方

重点检查：

- 哪些模块直接依赖被改模块
- 哪些类直接引用被改类型
- 哪些配置文件使用被改 key
- 哪些测试覆盖到该逻辑

### 4. 识别间接影响

例如：

- DTO 改动导致序列化 / 反序列化兼容问题
- constants 改动导致多个模块行为变化
- repository 变更导致 service 测试失效
- config 改动导致部署脚本、文档、env 文件需同步更新

### 5. 形成验证范围建议

根据影响面，建议至少验证：

- 直接受影响模块
- 依赖该公共模块的模块
- 契约发生变化的调用方
- 对应文档和脚本

## 输出格式建议

输出应包含：

- 改动模块
- 改动类型
- 直接影响模块
- 间接影响模块
- 需要同步检查的文件类型
- 建议验证命令
- 兼容性风险

## 重点关注点

### 公共模块改动
重点看：

- `core:constants`
- `core:kernel`
- `persistence:spec`

### 持久层改动
重点看：

- entity
- converter
- repository 接口
- schema
- SQL

### 配置改动
重点看：

- `application.properties`
- `env/*`
- 安装部署脚本
- README 与文档

## 禁止事项

- 不要只看当前修改文件而忽略调用方
- 不要只看编译通过就认定无影响
- 不要遗漏脚本、文档、配置同步问题