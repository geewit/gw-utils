你在 `gw-utils` 仓库中工作，默认遵循以下规则：

- 技术栈：Java 25、Gradle 9.x、多模块 Java Library、JUnit Jupiter、Jacoco、JReleaser。
- 仓库定位：`gw-utils` 是可发布到 Maven Central 的工具库，不是 Spring Boot 应用仓库。
- 模块边界以 `settings.gradle` 为准：
  - `core:*`：基础工具、编码、日期、枚举、异常、Jackson、语言扩展、反射、树结构、UUID。
  - `data:*`：数据通用对象与 Spring Data 适配能力。
  - `i18n`：国际化工具。
  - `javafx:*`：JavaFX 基础能力、控件与 Spring 集成工具。
  - `web:*`：Web 转换、核心对象与 JSON 支持。
- 先读取：`AGENTS.md`、`readme.md`、`.opencode/README.md`、`.opencode/prompts/repo_context.md`。
- 修改 Gradle 必须读取：`.opencode/skills/gradle-spec/SKILL.md`、`settings.gradle`、根 `build.gradle`、`gradle.properties`、`buildSrc/**`。
- 发布链路必须保留：`io.geewit.publish.enabled`、`maven-publish`、`signing`、`org.jreleaser`、`staging-deploy`。
- 统一使用 `gradle` 命令，不使用 `./gradlew`。
- 优先最小改动，只修改完成任务所需文件。
- 不把应用工程中的 app/server/persistence 架构强行引入 gw-utils。
- 新增公共 API 必须考虑二方/三方调用兼容性，避免无意义破坏性重命名。
- 新增或修复行为必须补最小必要测试，至少覆盖正常路径与一个边界/异常路径。
- 输出需包含：改动文件、验证命令、验证结果、潜在风险（如有）。
