# gw-utils OpenCode 配置

本目录是 gw-utils 当前 OpenCode 协作配置，按本仓库的多模块 Java 工具库边界维护。

默认目标：

- 保持 `gw-utils` 作为可发布的 Java library 多模块仓库。
- 修改 Gradle 时遵循本仓库 `buildSrc/common-*` 与 `buildSrc/gradle.properties` 的组织方式。
- 保留 JReleaser 发布链路，不破坏 Maven Central staging / deploy 流程。
- 代码改动优先最小化，按模块边界补充测试。

## Codex / OpenCode 共享

- Codex 入口位于 `.codex/**`。
- `.codex/prompts`、`.codex/rules`、`.codex/skills`、`.codex/templates` 指向 `.opencode/**` 公共源文件。
- OpenCode 主配置位于根目录 `opencode.jsonc`。
- `.opencode/opencode.json` 仅保留本地 plugin 基础配置。

## 已接入命令

- `flow`：先盘点真实仓库现状，再完成实现、测试与验证闭环。
- `code-quality-fix`：基于质量报告或静态分析输出修复 Java 代码质量问题。
- `upgrade-dependencies`：查询 Maven Central 最新 release 版本并更新 `gradle.properties`。

## 已接入规则

- `opencode.jsonc` 的 `instructions` 已接入 AGENTS、根 README、模块 prompts、编码规则、包边界规则、OpenSpec 工作流和各领域 skill。
- `reactor-spec` 已接入，用于 `javafx:base` 等当前存在 Reactor Core 使用的模块。
- `ddd-package-map.md` 仅描述 gw-utils 的包边界，不把工具库默认塑造成应用 DDD 分层。

## 未接入内容

应用工程中的 app/server/embedded/persistence、设备 SDK、WebSocket 协议、对象存储、微信、安全主链路等业务配置没有作为 gw-utils 默认配置保留。
