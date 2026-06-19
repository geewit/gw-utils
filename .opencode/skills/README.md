# gw-utils OpenCode Skills

## 已启用 skill

- `architecture-spec`：模块结构、公共 API、发布坐标、依赖边界。
- `java-spec`：普通 Java 工具源码、空值/集合/字符串处理、重复 helper 收敛、import 与废弃 API 清理。
- `code-quality-spec`：编译质量、静态分析报告、质量修复 prompt 与可选质量工具链。
- `modularization-spec`：JPMS、`module-info.java`、包名迁移、module-path 与发布兼容性。
- `gradle-spec`：Gradle 9.x、多模块构建、version catalog、buildSrc 自身构建与 JReleaser 发布链路。
- `javafx-spec`：JavaFX 工具库、控件、headless 测试、平台兼容。
- `spring-boot-spec`：Spring / Spring Boot 依赖使用边界，适用于 `data:spring`、`javafx:spring` 等工具模块。
- `reactor-spec`：Reactor Core 工具使用边界，适用于 `javafx:base` 等引入 Reactor 的模块。
- `testing-spec`：JUnit Jupiter、Mockito、AssertJ、TestFX 与 Jacoco。
- `id-spec`：UUID / ID 工具规则。
- `graalvm-spec`：native image、反射提示、工具库可达性元数据。
- `openspec-spec`：文档、prompt、skill 与代码现状同步。

## 已裁剪内容

应用工程中偏业务服务或设备域的 `app`、`server`、`embedded`、`persistence:*`、`websockets`、`security`、`wechat`、`object-storage`、SDK 接入等技能没有作为 gw-utils 默认技能保留，避免把应用架构误导入工具库。
