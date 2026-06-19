# Codex 等价指令清单

本文件把仓库级协作约束整理为 Codex 可读入口。具体任务仍以 `AGENTS.md`、`opencode.jsonc`、`.opencode/**` 和真实代码为准。

## 默认 agent 行为

- 仓库任务默认按 `.codex/commands/flow.md` 执行：先盘点真实模块、源码、测试与构建配置，再按任务相关性读取 prompts / rules / skills。
- 简单问答或状态查询可以轻量化执行，但不能跳过必要的现状确认。
- 默认使用中文说明；代码、命令、日志、错误信息保持原文。
- 只做本次任务需要的最小闭环，不做无关重构。
- 文档与代码冲突时，先按代码确认现状，再修正文档。
- 输出必须包含修改文件、验证命令、验证结果、剩余风险。

## 常用入口

- `AGENTS.md`
- `opencode.jsonc`
- `.codex/README.md`
- `.codex/commands/flow.md`
- `.codex/commands/code-quality-fix.md`
- `.codex/commands/upgrade-dependencies.md`
- `.codex/templates/flow.md`
- `.codex/prompts/system.md`
- `.codex/prompts/repo_context.md`
- `.codex/prompts/test_strategy.md`
- `.codex/prompts/doc-routing.md`
- `.codex/prompts/module_core_utils.md`
- `.codex/prompts/module_data_utils.md`
- `.codex/prompts/module_i18n.md`
- `.codex/prompts/module_javafx_utils.md`
- `.codex/prompts/module_web_utils.md`
- `.codex/prompts/fix-code-quality.md`
- `.codex/rules/coding-rules.md`
- `.codex/rules/ddd-package-map.md`
- `.codex/rules/openspec-workflow.md`
- `.codex/skills/architecture-spec/SKILL.md`
- `.codex/skills/java-spec/SKILL.md`
- `.codex/skills/code-quality-spec/SKILL.md`
- `.codex/skills/modularization-spec/SKILL.md`
- `.codex/skills/gradle-spec/SKILL.md`
- `.codex/skills/spring-boot-spec/SKILL.md`
- `.codex/skills/reactor-spec/SKILL.md`
- `.codex/skills/javafx-spec/SKILL.md`
- `.codex/skills/testing-spec/SKILL.md`
- `.codex/skills/openspec-spec/SKILL.md`
- `.codex/skills/graalvm-spec/SKILL.md`
- `.codex/skills/id-spec/SKILL.md`

## 忽略与扫描边界

不要主动扫描以下目录，除非任务明确要求：

```text
.git/
.gradle/
.idea/
.settings/
.cache/
.temp/
bin/
build/
coverage/
dist/
node_modules/
out/
target/
```

例外：Gradle、依赖或文档任务可以读取受管构建配置，例如 `settings.gradle`、`gradle.properties`、各模块 `build.gradle`、`buildSrc/**`。

## 权限与高风险操作

- 禁止执行 `rm -rf *` 一类破坏性命令。
- `git push` 必须由用户明确要求或批准。
- 构建、测试、检查命令统一直接使用 `gradle`。
