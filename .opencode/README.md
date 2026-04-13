# OpenCode Workspace Notes

本目录用于补充 OpenCode 在当前仓库中的命令、技能与执行约束，内容应与根目录 `AGENTS.md` 保持一致。

## 目录结构

- `command/`：可直接触发的命令说明
- `skills/`：在特定任务场景下应加载的技能说明

## 建议阅读顺序

1. 根目录 `AGENTS.md`
2. `.opencode/README.md`
3. 当前文件
4. 按任务需要读取：
   - `.opencode/commands/*.md`
   - `.opencode/skills/*/SKILL.md`

## 使用原则

- 以当前用户请求为准，不沿用历史单次任务假设
- 先扫描仓库现状，再做最小范围增量改造
- 不基于猜测修改代码或文档
- 不回滚上一版仍有效的改动

## 命令约束

所有构建、测试、检查命令统一直接使用 `gradle`，禁止使用 `./gradlew`。

执行前建议显式设置：

```bash
export JAVA_HOME=/Users/geewit/.sdkman/candidates/java/25.0.2-graal
export GRADLE_HOME=/Users/geewit/.sdkman/candidates/gradle/current
export PATH="$JAVA_HOME/bin:$GRADLE_HOME/bin:$PATH"
```

## 文档维护约定

- 技能文件统一命名为 `SKILL.md`
- 命令文档中的示例命令、工具名、交互方式应与当前平台实际能力一致
- 如需补充新命令或新技能，优先复用 `AGENTS.md` 中已有规则，避免两套规范长期漂移
