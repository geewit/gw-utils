# gw-utils OpenCode Skills

## 已启用 skill

- `architecture-spec`：模块结构、公共 API、发布坐标、依赖边界。
- `gradle-spec`：Gradle 9.x、多模块构建、version catalog、JReleaser 发布链路。
- `javafx-spec`：JavaFX 工具库、控件、headless 测试、平台兼容。
- `spring-boot-spec`：Spring / Spring Boot 依赖使用边界，适用于 `data:spring`、`javafx:spring` 等工具模块。
- `reactor-spec`：Reactor Core 工具使用边界，适用于 `javafx:base` 等引入 Reactor 的模块。
- `testing-spec`：JUnit Jupiter、Mockito、AssertJ、TestFX 与 Jacoco。
- `id-spec`：UUID / ID 工具规则。
- `graalvm-spec`：native image、反射提示、工具库可达性元数据。
- `openspec-spec`：文档、prompt、skill 与代码现状同步。

## 已裁剪内容

friso 中偏应用服务的 `server`、`persistence:r2dbc`、`websockets`、`security`、`wechat`、`object-storage` 等技能没有作为 gw-utils 默认技能保留，避免把应用架构误导入工具库。
