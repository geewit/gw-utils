# code-quality-fix

基于质量报告或静态分析输出，使用 Codex 尝试修复 gw-utils 的 Java 代码质量问题。

执行时读取并遵守：

1. `AGENTS.md`
2. `.codex/README.md`
3. `.codex/instructions.md`
4. `.codex/prompts/fix-code-quality.md`
5. `.codex/skills/code-quality-spec/SKILL.md`
6. `.codex/skills/java-spec/SKILL.md`
7. `.codex/skills/gradle-spec/SKILL.md`
8. `.codex/skills/testing-spec/SKILL.md`

修复规则：

- 优先读取用户提供的质量报告、编译错误、测试失败或 `.codex/code-quality/**` 临时报告。
- 若仓库后续接入 OpenRewrite / Error Prone / NullAway / SpotBugs / PMD / Checkstyle / ArchUnit，按报告顺序逐项修复。
- 只做与报告直接相关的最小改动。
- 不为了消除告警随意添加抑制注解、空 catch 或无意义默认值。
- 修改行为时必须补充或更新最小测试。

验证建议：

```bash
gradle :core:uuid:test
gradle :core:jackson:test
gradle :data:spring:test
gradle :javafx:base:test
gradle :web:json:test
```

按命中模块选择最小命令，必要时扩大到 `gradle test`。
