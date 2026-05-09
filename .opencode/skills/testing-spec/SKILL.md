---
name: testing-spec
description: 在 gw-utils 仓库中新增、修改或修复测试时必须先加载本技能。
compatibility: opencode
metadata:
  domain: testing
  purpose: junit-and-module-test-safety
---

# testing-spec

## 默认测试栈

- JUnit Jupiter
- Mockito
- AssertJ（已有模块使用时）
- Jacoco
- TestFX（仅 JavaFX 模块需要）

## 测试原则

1. 新增行为必须补测试。
2. 修复 bug 必须补能复现 bug 的测试。
3. 公共工具类必须覆盖边界条件。
4. 不为测试方便破坏公共 API。
5. 不为测试方便新增应用级 Spring Boot 启动测试。
6. 测试命令统一使用 `gradle`。

## 场景规则

### core 工具

- 覆盖 null、空字符串、空集合、非法参数、边界值。
- 日期/时间工具避免依赖系统当前时间；必要时注入 `Clock`、`ZoneId`。
- UUID/ID 工具测试不能依赖固定执行顺序导致不稳定。

### Jackson / JSON

- 覆盖序列化、反序列化、未知字段、空字段。
- 字段兼容性变更必须显式测试。

### JavaFX

- 非 UI 逻辑优先下沉并单测。
- UI 测试保留 headless 参数。
- 不把测试写成只能在当前本机图形环境运行。

### Gradle / 发布

- 构建逻辑变更至少验证 `gradle projects`。
- 发布逻辑变更至少给出 `publishToMavenLocal` 或 `publishAllPublicationsToJreleaserRepository` 验证命令。

## 推荐命令

```shell
gradle :core:uuid:test
gradle :core:jackson:test
gradle :data:spring:test
gradle :javafx:base:test
gradle :web:json:test
```

## 输出要求

- 新增/修改了哪些测试。
- 覆盖了哪些正常/边界/异常路径。
- 执行了哪些验证命令。
- 当前环境无法执行时说明原因。
