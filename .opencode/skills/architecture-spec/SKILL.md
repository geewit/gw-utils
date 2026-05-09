---
name: architecture-spec
description: 在 gw-utils 仓库中修改模块划分、公共 API、包结构、模块依赖或跨模块复用关系时必须先加载本技能。
compatibility: opencode
metadata:
  domain: architecture
  purpose: module-boundary-and-public-api-safety
---

# architecture-spec

## 仓库定位

`gw-utils` 是多模块 Java 工具库，不是应用服务仓库。架构目标是保持工具库边界清晰、发布坐标稳定、公共 API 可复用。

## 模块分组

```text
core:*       基础工具能力
data:*       数据通用对象与 Spring Data 适配
i18n         国际化工具
javafx:*     JavaFX 通用工具与控件
web:*        Web / JSON 通用工具
```

## 边界规则

1. `core:*` 是底层基础库，不依赖上层 `data:*`、`javafx:*`、`web:*`。
2. `data:commons` 不依赖 Spring。
3. `data:spring` 可以依赖 Spring Data Commons。
4. `javafx:*` 不把业务页面、业务 ViewModel、业务 Controller 写入工具库。
5. `web:*` 不写具体业务接口路径、业务鉴权主流程。
6. 任意模块不得依赖不存在于 `settings.gradle` 的应用模块。

## 公共 API 规则

- 新增 API 优先小而稳定。
- 变更已有 public/protected 类型、方法、字段前必须评估兼容性。
- 能新增重载，不删除旧签名。
- 异常行为、null 行为、空集合行为必须可测试。

## 发布坐标规则

- 子模块发布坐标来自本模块 `gradle.properties`：`group` + `artifactId`。
- 不因重构随意改 artifactId。
- 如果移动包名或模块，必须说明迁移成本。

## 输出要求

- 涉及哪些模块。
- 是否改变模块依赖方向。
- 是否影响公共 API。
- 是否影响 Maven 发布坐标。
- 需要执行的最小测试命令。
