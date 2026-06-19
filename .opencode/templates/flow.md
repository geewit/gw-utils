# gw-utils Flow Template

基于当前命令或用户输入的任务内容：

```markdown
$ARGUMENTS
```

## 总原则

1. 先盘点真实仓库现状，再决定是否设计、实现或验证。
2. 只做本次任务需要的最小闭环，不做无关重构。
3. 遵守 `AGENTS.md`、`readme.md`、`.opencode/**` 与当前源码/测试。
4. 默认使用中文说明；代码、命令、日志、错误信息保持原文。
5. 统一使用 `gradle`，不使用 `./gradlew`。

## Phase 0：读取规则与仓库扫描

至少读取：

1. `AGENTS.md`
2. `readme.md`
3. `opencode.jsonc`
4. `.opencode/README.md`
5. `.codex/README.md` 与 `.codex/instructions.md`（Codex 场景）
6. `.opencode/prompts/repo_context.md`
7. 当前任务最相关的源码、测试、配置、Gradle 模块

识别：

- 已纳入 `settings.gradle` 的模块。
- 当前变更是否影响公共 API、发布坐标、JReleaser、version catalog。
- 当前变更属于 core/data/i18n/javafx/web 哪个模块域。
- 当前变更是否需要 `java-spec`、`code-quality-spec`、`modularization-spec`、`reactor-spec` 等通用技能。

## Phase 1：当前状态盘点

输出：

- 已实现
- 部分实现
- 已实现但与设计不一致
- 未实现
- 无法判断

并收敛本轮真正需要处理的最小任务清单。

## Phase 2：设计与规格收敛

当文档与代码不一致、规格不足或任务本身要求设计时，更新：

- `AGENTS.md`
- `.opencode/prompts/*.md`
- `.opencode/skills/*/SKILL.md`
- `.codex/**` 中的 Codex 入口或命令映射
- 必要的 `docs/**/*.md`

不要把临时聊天结论当作长期规格。

## Phase 3：实现

1. 按模块职责落代码。
2. `core:*` 不依赖 `javafx:*`、`web:*`、应用业务模块。
3. 公共 API 变更必须说明兼容性影响。
4. 修改 Gradle 时保留 `maven-publish`、`signing`、`org.jreleaser`。
5. 子模块发布坐标以本模块 `gradle.properties` 的 `group` / `artifactId` 为准。

## Phase 4：验证

根据改动范围优先执行最小验证：

```shell
gradle projects
gradle :core:uuid:test
gradle :core:jackson:test
gradle :javafx:base:test
gradle publishToMavenLocal -Pio.geewit.publish.enabled=false
```

若当前环境不能执行 Gradle，应至少检查关键路径、类名、配置项、version catalog alias 与文档入口是否一致。

## Phase 5：汇报

汇报必须包含：

1. 修改了什么。
2. 为什么这么改。
3. 验证了什么。
4. 是否影响发布坐标或 JReleaser。
5. 仍需人工选择或后续处理的事项。
