---
name: code-quality-spec
description: 在 gw-utils 仓库中处理编译质量、静态分析报告、OpenRewrite、Error Prone、NullAway、SpotBugs、PMD、Checkstyle、ArchUnit、quality-scan 或 Codex/OpenCode 自动修复 prompt 时必须加载本技能。
compatibility: opencode
metadata:
  domain: quality
  purpose: static-analysis-and-agent-fix-loop
---

# code-quality-spec

## 适用场景

- 根据编译错误、测试失败或静态分析报告修复 Java 代码质量问题。
- 新增或维护 `.opencode/prompts/fix-code-quality.md`。
- 新增或调整 OpenRewrite、Error Prone、NullAway、SpotBugs、PMD、Checkstyle、ArchUnit 等质量工具链。
- 根据 `.codex/code-quality/**` 或 `build/reports/**` 修复问题。

## 工具链边界

1. gw-utils 当前没有默认质量扫描脚本时，不要把不存在的 `scripts/quality-scan.sh` 写成必跑入口。
2. 如果后续接入质量工具链，应默认不改变普通 `gradle test` 基线；质量扫描通过显式参数、init script 或专用任务启用。
3. 普通构建、测试、文档中的命令都必须直接使用 `gradle`，禁止写 Gradle wrapper 命令。
4. 版本号来源继续是 `gradle.properties`；插件声明继续在 `buildSrc/common-settings.gradle`。
5. 临时扫描产物可放入 `.codex/code-quality/`，该目录不作为长期文档提交。

## 修复优先级

1. 编译错误与测试失败。
2. OpenRewrite patch 中的 import、格式、静态分析 recipe 和 JUnit 建议。
3. Error Prone / NullAway 的真实 bug 或 NPE 风险。
4. SpotBugs 的字节码级 bug、资源释放、并发、equals/hashCode、返回值误用。
5. PMD 的空 catch、无用代码、复杂度、性能和明显坏味道。
6. Checkstyle 的 import、括号、空代码块、tab 和文件末尾换行。
7. ArchUnit 的模块边界问题。

## 禁止事项

- 禁止为了清空报告随意添加 `@SuppressWarnings`、`// noinspection`、空 catch 或无意义 null 默认值。
- 禁止通过降低断言、跳过测试、删除参数校验来通过质量扫描。
- 禁止借质量修复做无关的大规模格式化、包迁移、公共 API 重命名。
- 禁止引入 Spring MVC / Servlet / JPA / Hibernate / HikariCP 作为工具库默认依赖。
- 禁止把 `build/reports/**` 或 `.codex/code-quality/**` 当作长期文档提交。

## 推荐验证

按命中模块选择最小验证：

```bash
gradle :core:uuid:test
gradle :core:jackson:test
gradle :data:spring:test
gradle :javafx:base:test
gradle :web:json:test
```

构建或发布配置受影响时补充：

```bash
gradle projects
gradle publishToMavenLocal -Pio.geewit.publish.enabled=false
```

## 输出要求

- 说明读取了哪些质量报告。
- 说明修复项属于编译、测试、OpenRewrite、Error Prone、NullAway、SpotBugs、PMD、Checkstyle、ArchUnit 中哪一类。
- 说明是否修改了公共 API 或行为；若修改行为，必须列出测试覆盖。
- 说明验证命令与真实结果。
- 说明剩余风险与需要人工判断的报告项。
