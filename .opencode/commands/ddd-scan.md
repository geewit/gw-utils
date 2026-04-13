---
description: 扫描当前仓库的 DDD 现状与边界混乱点
agent: plan
---

请先阅读：
- @AGENTS.md
- @.opencode/rules/ddd-package-map.md

然后扫描当前仓库，输出：

1. 当前模块与主要 package 分布
2. 当前已存在的上下文雏形
3. 当前混杂最严重的模块/包
4. DTO / Entity / Persistence Model / Domain Model 是否混用
5. 哪些地方存在：
   - Controller/Handler 承担领域规则
   - Repository 直接承载业务规则
   - 宽表继续膨胀
   - 跨模块直接复用内部实现
6. 最适合先做 DDD 收敛的三个切入点

要求：
- 只分析，不直接改代码
- 结论必须引用真实文件/包路径
- 如仓库现状和 `ddd-package-map.md` 不一致，以仓库现状为准，再给出演进建议
