# 文档路由提示

你在 gw-utils 仓库中工作时，默认不能只依赖根目录文档。

## 默认读取顺序

1. `AGENTS.md`
2. `readme.md`
3. `opencode.jsonc`
4. `.opencode/README.md`
5. `.codex/README.md` 与 `.codex/instructions.md`（Codex 场景）
6. `.opencode/prompts/repo_context.md`
7. 与当前任务相关的 `.opencode/prompts/*.md` / `.codex/prompts/*.md`
8. 与当前任务相关的 `.opencode/skills/*/SKILL.md` / `.codex/skills/*/SKILL.md`

## 任务到文档的映射

### Gradle / 发布 / JReleaser

读取：

- `settings.gradle`
- `build.gradle`
- `gradle.properties`
- `buildSrc/build.gradle`
- `buildSrc/gradle.properties`
- `buildSrc/common-settings.gradle`
- `buildSrc/common-resolution-strategy.gradle`
- `.opencode/skills/gradle-spec/SKILL.md`

### core 工具模块

读取：

- `.opencode/prompts/module_core_utils.md`
- `.opencode/skills/testing-spec/SKILL.md`
- 相关模块源码与测试

### JavaFX 工具模块

读取：

- `.opencode/prompts/module_javafx_utils.md`
- `.opencode/skills/javafx-spec/SKILL.md`
- `.opencode/skills/testing-spec/SKILL.md`

如果涉及 Mono / Flux / Scheduler / 阻塞隔离，再读：

- `.opencode/skills/reactor-spec/SKILL.md`

### web / json 工具模块

读取：

- `.opencode/prompts/module_web_utils.md`
- `.opencode/skills/testing-spec/SKILL.md`

### 数据 / Spring Data 工具模块

读取：

- `.opencode/prompts/module_data_utils.md`
- `.opencode/skills/spring-boot-spec/SKILL.md`
- `.opencode/skills/testing-spec/SKILL.md`

### i18n 工具模块

读取：

- `.opencode/prompts/module_i18n.md`
- `.opencode/skills/testing-spec/SKILL.md`

## 禁止

- 不把应用工程的 `app/server/persistence` 必读文件照搬为 gw-utils 默认必读。
- 不引用当前仓库不存在的文档作为强制读取项。
- 不新增 `./gradlew` 命令示例。
