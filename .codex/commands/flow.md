# flow

先盘点 gw-utils 现状，再按工具库边界完成增量实现、测试与验证。

执行时读取并遵守：

1. `AGENTS.md`
2. `opencode.jsonc`
3. `.opencode/opencode.json`
4. `.codex/README.md`
5. `.codex/instructions.md`
6. `.codex/templates/flow.md`
7. 与任务相关的 prompts / rules / skills

实现闭环：

- Phase 0：读取规则与仓库扫描
- Phase 1：当前状态盘点
- Phase 2：设计与规格收敛
- Phase 3：实现或文档更新
- Phase 4：补测试或补验证入口
- Phase 5：运行最小必要验证
- Phase 6：最终输出修改文件、验证命令、验证结果、剩余风险

特别约束：

- 不把 app/server/persistence 业务架构引入 gw-utils。
- 不破坏已有 Maven artifact 的 groupId / artifactId / version 发布链路。
- 保留 JReleaser、Maven Publish、Signing 与 `io.geewit.publish.enabled`。
- 命令统一使用 `gradle`。
