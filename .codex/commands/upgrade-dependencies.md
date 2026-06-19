# upgrade-dependencies

自动检查 `gradle.properties` 中的依赖版本号，查询 Maven Central 最新 release 版本并按需更新。

预览：

```bash
node .opencode/scripts/upgrade-dependencies.js --dry-run
```

执行：

```bash
node .opencode/scripts/upgrade-dependencies.js --yes
```

更新后必须执行相关 `gradle` 编译或测试验证。默认只选取 release 版本；不要自行切换 JDK 或 Gradle 版本。
