# gw-utils 测试策略

- 单元测试：JUnit Jupiter。
- 断言：优先使用现有 AssertJ / JUnit Assertions 风格，不为单个测试引入新断言框架。
- Mock：已有 Mockito 时可使用 Mockito，不为简单值对象过度 mock。
- 公共工具类：必须覆盖正常路径、空值/非法参数、边界值。
- UUID / ID / 时间相关工具：测试中避免依赖系统当前时间导致不稳定。
- Jackson / JSON 工具：覆盖序列化、反序列化、未知字段、空字段等场景。
- JavaFX 工具：优先把非 UI 逻辑下沉到可单测服务；涉及 UI 的测试保持 headless 参数。
- Gradle / 发布配置变更：至少检查 `gradle projects`、目标模块 `compileJava`、目标模块 `test`；发布相关变更补充 `publishToMavenLocal` 或 JReleaser staging 验证命令。

禁止：

- 为了测试方便破坏公共 API。
- 为了测试方便把平台相关代码写死为 macOS 或 Windows。
- 在工具库中引入应用级 Spring Boot 启动测试作为默认测试方式。
