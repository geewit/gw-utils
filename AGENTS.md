# AGENTS.md

## 目标

本仓库的 AI 协作目标是维护 `gw-utils` 作为稳定、可测试、可发布的 Java 工具库集合。

所有变更必须同时满足：

- 与真实代码、`settings.gradle`、模块 `build.gradle` 一致。
- 不破坏已有 artifact 的 groupId / artifactId / version 发布链路。
- 不把应用工程中的 app/server 业务架构强行引入工具库。
- 保留 JReleaser 发布能力。

## 当前仓库边界

### Gradle 模块

以 `settings.gradle` 为准：

- `:core:codec`
- `:core:date`
- `:core:enums`
- `:core:exceptions`
- `:core:jackson`
- `:core:lang`
- `:core:reflection`
- `:core:tree`
- `:core:uuid`
- `:data:commons`
- `:data:spring`
- `:i18n`
- `:javafx:base`
- `:javafx:controls`
- `:javafx:spring`
- `:web:converter`
- `:web:core`
- `:web:json`

## 当前构建基线

- Java：`io.geewit.java.version=25`
- Gradle：使用系统 `gradle` 命令
- 版本目录：`buildSrc/common-settings.gradle`
- 依赖对齐：`buildSrc/common-resolution-strategy.gradle`
- 发布：`maven-publish` + `signing` + `org.jreleaser`
- 发布开关：`io.geewit.publish.enabled`
- staging 仓库：各模块 `build/staging-deploy`

## 模块职责

### core

- `core:codec`：编码/解码工具。
- `core:date`：日期时间工具。
- `core:enums`：枚举基础能力。
- `core:exceptions`：通用异常。
- `core:jackson`：Jackson/JSON 基础支持。
- `core:lang`：语言级通用工具。
- `core:reflection`：反射工具。
- `core:tree`：树结构工具。
- `core:uuid`：UUID / ID 工具。

### data

- `data:commons`：轻量数据通用对象。
- `data:spring`：Spring Data Commons 适配能力。

### i18n

- 国际化消息、资源与 MessageSource 相关工具。

### javafx

- `javafx:base`：JavaFX 基础工具。
- `javafx:controls`：通用控件能力。
- `javafx:spring`：JavaFX 与 Spring 集成工具。

### web

- `web:converter`：Web 转换器。
- `web:core`：Web 通用对象。
- `web:json`：Web JSON 支持。

## Agent 文档读取规则

### 全局默认读取顺序

1. `AGENTS.md`
2. `readme.md`
3. `.opencode/README.md`
4. `.opencode/prompts/repo_context.md`
5. 与当前任务相关的 `.opencode/prompts/*.md`
6. 与当前任务相关的 `.opencode/skills/*/SKILL.md`

### 任务专项规则

#### Gradle / buildSrc / 发布

必须先读：

- `settings.gradle`
- `build.gradle`
- `gradle.properties`
- `buildSrc/common-settings.gradle`
- `buildSrc/common-resolution-strategy.gradle`
- `.opencode/skills/gradle-spec/SKILL.md`

#### core 工具模块

必须先读：

- `.opencode/prompts/module_core_utils.md`
- `.opencode/skills/testing-spec/SKILL.md`

#### data / Spring Data 工具模块

必须先读：

- `.opencode/prompts/module_data_utils.md`
- `.opencode/skills/spring-boot-spec/SKILL.md`
- `.opencode/skills/testing-spec/SKILL.md`

#### JavaFX 工具模块

必须先读：

- `.opencode/prompts/module_javafx_utils.md`
- `.opencode/skills/javafx-spec/SKILL.md`
- `.opencode/skills/testing-spec/SKILL.md`

#### web / json 工具模块

必须先读：

- `.opencode/prompts/module_web_utils.md`
- `.opencode/skills/testing-spec/SKILL.md`

## Skill 激活总原则

- 修改 Gradle：激活 `gradle-spec`。
- 写 JavaFX 工具：激活 `javafx-spec`。
- 写 Spring 相关工具：激活 `spring-boot-spec`。
- 写测试：激活 `testing-spec`。
- 修改 ID / UUID：激活 `id-spec`。
- 修改 GraalVM/native image 支持：激活 `graalvm-spec`。
- 修改文档/规范：激活 `openspec-spec`。

## 不允许的做法

- 不允许使用 `./gradlew`。
- 不允许把 friso 的 `app/server/persistence` 业务模块引入 gw-utils。
- 不允许破坏 `group`、`artifactId`、`POM_*`、JReleaser 发布配置。
- 不允许为了单个模块方便而新增平行版本管理机制。
- 不允许在工具库中引入应用启动主流程作为默认测试方式。
- 不允许扫描或修改 `build/`、`.gradle/`、`bin/`、`target/` 等生成目录。

## 实施约束

### Java / Gradle

- 统一使用 `gradle`。
- 依赖版本优先进入 version catalog 与 `gradle.properties`。
- 模块依赖变更必须检查是否影响 artifact 发布。
- 子模块 `gradle.properties` 中的 `group` / `artifactId` 是发布真相源。

### 公共 API

- 公共方法、类、包名、artifactId 的变更必须说明兼容性影响。
- 能新增重载就不要轻易破坏旧签名。
- 工具类必须清晰处理 null、空集合、非法参数。

### JavaFX

- UI 更新必须在 JavaFX Application Thread。
- 通用控件逻辑不要携带具体业务语义。
- 平台相关依赖必须兼容 Windows x64、macOS Intel、macOS Apple Silicon。

### 测试

- 新增行为必须补测试。
- 修复 bug 必须新增能复现 bug 的测试。
- 测试命令统一使用 `gradle :module:test`。

## 默认执行顺序

1. 先扫描真实模块、源码、测试与构建配置。
2. 再读取相关 prompts / skills。
3. 判断是否影响发布坐标、公共 API 或模块依赖方向。
4. 做最小增量改动。
5. 运行或给出精确验证命令。
6. 汇报改动文件、验证结果与风险。

## 完成标准

一次任务完成时，至少应满足：

- 代码与构建配置不矛盾。
- JReleaser 发布链路没有被破坏。
- 相关模块测试可执行。
- 输出包含验证命令。
