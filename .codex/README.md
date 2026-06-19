# gw-utils Codex 配置

本目录是 gw-utils 仓库的 Codex 入口，用来复用 `AGENTS.md` 与 `.opencode/**` 中的协作约束。

## 共享原则

- `AGENTS.md` 是仓库最高优先级协作入口。
- OpenCode 的 prompts / rules / skills 是公共源文件。
- Codex 通过本目录下的链接读取同一份 markdown，避免维护第二套规则。
- 修改共享规则时，优先编辑 `.opencode/**` 下的源文件；`.codex/prompts`、`.codex/rules`、`.codex/skills`、`.codex/templates` 指向这些源文件。

## Codex 读取顺序

1. `AGENTS.md`
2. `opencode.jsonc`
3. `.opencode/opencode.json`
4. 本文件 `.codex/README.md`
5. `.codex/instructions.md`
6. `.codex/commands/flow.md` 与 `.codex/templates/flow.md`
7. `readme.md`
8. 与任务相关的 `.codex/prompts/*.md`
9. 与任务相关的 `.codex/rules/*.md`
10. 与任务相关的 `.codex/skills/*/SKILL.md`

## 目录映射

| Codex 路径 | 公共源文件 | 用途 |
|------------|------------|------|
| `.codex/prompts` | `.opencode/prompts` | 仓库、模块、测试、路由提示 |
| `.codex/rules` | `.opencode/rules` | 编码、模块边界、OpenSpec 规则 |
| `.codex/skills` | `.opencode/skills` | gw-utils 工具库场景 skill |
| `.codex/templates` | `.opencode/templates` | flow 等任务模板 |

## 命令映射

- `.codex/commands/flow.md`
- `.codex/commands/code-quality-fix.md`
- `.codex/commands/upgrade-dependencies.md`

代码质量修复能力复用 `.opencode` 公共源文件：

- 共享 prompt：`.codex/prompts/fix-code-quality.md` -> `.opencode/prompts/fix-code-quality.md`
- 共享 skill：`.codex/skills/code-quality-spec/SKILL.md` -> `.opencode/skills/code-quality-spec/SKILL.md`

依赖升级能力复用：

- `.opencode/scripts/upgrade-dependencies.js`

构建和测试命令必须直接使用 `gradle`，禁止使用 `./gradlew`。
