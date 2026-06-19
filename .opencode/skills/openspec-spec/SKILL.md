---
name: openspec-spec
description: 在 gw-utils 仓库中同步 AGENTS、readme、docs、.opencode 规则、prompt 与 skill，并保证文档反映当前代码与交付优先级时必须加载本技能。
compatibility: opencode
metadata:
  domain: spec
  purpose: current-state-doc-sync
---

# openspec-spec

## 适用场景

- 更新 `AGENTS.md`
- 更新 `readme.md`
- 更新 `docs/**/*.md`
- 更新 `.opencode/skills/*`
- 更新 `.opencode/prompts/*`
- 更新 `.opencode/rules/*`
- 更新 `.codex/**`
- 更新 `opencode.jsonc`
- 将代码现状、模块结构、发布配置同步到文档

## 文档边界

1. 文档必须以 gw-utils 工具库现状为准。
2. 不把应用工程的 app/server/persistence 业务架构写成 gw-utils 当前架构。
3. 构建/发布文档必须保留 JReleaser、Maven Publish、Signing、POM 元数据。
4. 文档中的命令统一使用 `gradle`，不要写 `./gradlew`。

## 核心规则

1. 现状优先服从代码：文档不得声明当前代码没有实现的能力为已完成。
2. TODO 只保留未完成事项。
3. 合并重复 skill：相同职责的规则写入同一个 skill，避免冲突。
4. 文档更新必须同步检查 `opencode.jsonc` 的 `instructions`。
5. 新增文档或 skill 时必须保证路径真实存在。

## 必查文件

- `AGENTS.md`
- `readme.md`
- `opencode.jsonc`
- `.opencode/README.md`
- `.opencode/skills/README.md`
- `.codex/README.md`
- `.codex/instructions.md`
- 当前涉及模块的 `build.gradle`

## 输出要求

- 说明更新了哪些 root 文档 / codex 入口 / skill / prompt。
- 说明文档对应的代码现状。
- 如新增 skill，必须说明是否已接入 `opencode.jsonc`。
