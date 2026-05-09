# gw-utils 文档工作流

## 当前文档入口

- `AGENTS.md`
- `readme.md`
- `.opencode/README.md`
- `.opencode/prompts/*.md`
- `.opencode/skills/*/SKILL.md`
- `docs/**/*.md`（如存在）

## 工作原则

1. 先基于真实代码、`settings.gradle`、模块 `build.gradle` 判断当前状态。
2. 再同步 `AGENTS.md`、prompt、skill 或 docs。
3. 文档必须反映 gw-utils 工具库现状，不照搬应用服务架构。
4. TODO 只保留未完成事项。
5. 命令统一使用 `gradle`。

## 文档更新规则

### 当任务是“修正文档”

直接更新相关 root 文档、prompt 或 skill。

### 当任务是“设计能力”

把长期规则沉淀到 `.opencode/prompts` 或 `.opencode/skills`，避免散落在临时文件。

### 当任务是“实现代码”

实现前盘点当前模块边界；实现后同步必要文档与测试说明。

## 禁止事项

- 不新增阶段性留档文档。
- 不把已完成事项写回 TODO 主体。
- 不引用当前仓库不存在的文档作为强制入口。
