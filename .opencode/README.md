# gw-utils OpenCode 配置

本目录由 friso 的 OpenCode 体系迁移而来，并按 gw-utils 的多模块 Java 工具库边界做了裁剪与适配。

默认目标：

- 保持 `gw-utils` 作为可发布的 Java library 多模块仓库。
- 修改 Gradle 时遵循 friso 的 `buildSrc/common-*` 风格。
- 保留 JReleaser 发布链路，不破坏 Maven Central staging / deploy 流程。
- 代码改动优先最小化，按模块边界补充测试。
