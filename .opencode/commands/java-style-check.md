---
description: 检查 Java 文件是否使用 import 而不是完整限定类名
agent: plan
---

先读取：

- @AGENTS.md
- `.opencode/skills/java-import-style/SKILL.md`

然后检查当前改动涉及的 Java 文件：

1. 是否存在不必要的完整限定类名（FQCN）
2. 是否应该改为 `import`
3. 是否存在同名冲突导致必须保留 FQCN
4. 给出最小改动建议
5. 不直接改代码，除非我明确要求