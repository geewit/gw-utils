---
name: modularization-spec
description: 在 gw-utils 仓库中处理 Java JPMS 模块化、module-info.java、包名迁移、Gradle inferModulePath、模块依赖边界、JavaFX module-path 或 GraalVM native image 模块化联动时必须加载本技能。
compatibility: opencode
metadata:
  domain: modularization
  purpose: jpms-gradle-native-library-modules
---

# modularization-spec

## 目标

把模块化当成一条可验证的工具库发布链路处理，而不是只补 `requires`。

必须同时维护：

1. Java 包名与 `module-info.java`
2. Gradle 依赖、toolchain、`modularity.inferModulePath`
3. JavaFX 模块路径与 headless 测试
4. GraalVM native image 反射、资源、JNI 与代理元数据
5. Maven artifact 的 groupId / artifactId / public API 兼容性

## 联动技能

按场景追加：

- Gradle 脚本：`gradle-spec`
- 普通 Java 迁移：`java-spec`
- 模块依赖边界：`architecture-spec`
- Spring Data / Spring Boot autoconfigure：`spring-boot-spec`
- JavaFX：`javafx-spec`
- Reactor：`reactor-spec`
- GraalVM native image / AOT hints：`graalvm-spec`
- 测试与验证闭环：`testing-spec`

## 必读文件

先读全局与构建入口：

```text
AGENTS.md
settings.gradle
build.gradle
gradle.properties
buildSrc/common-settings.gradle
buildSrc/common-resolution-strategy.gradle
```

再读涉及模块的 `build.gradle`、`gradle.properties` 与源码。

每次都先确认是否已经存在模块描述符：

```bash
rg --files -g 'module-info.java'
```

## 当前 Gradle 模块

真实模块以 `settings.gradle` 为准：

```text
:core:codec
:core:date
:core:enums
:core:exceptions
:core:jackson
:core:lang
:core:reflection
:core:tree
:core:uuid
:data:commons
:data:spring
:i18n
:javafx:base
:javafx:controls
:javafx:spring
:web:converter
:web:core
:web:json
```

## 执行顺序

1. 用 `settings.gradle` 确认真实 Gradle 模块，不凭目录名猜测。
2. 用 `rg --files -g 'module-info.java'` 找出现有模块描述符。
3. 统计涉及模块 `src/main/java` 的真实 package，确认 module name 是否与发布 artifact 和主包根一致。
4. 先确认 Gradle `dependencies` 是否已有依赖，再判断是否只是 JPMS 缺 `requires`。
5. 修改 `module-info.java` 时只补真实需要的 `requires` / `exports` / `opens`，不要为消除一个错误导出整棵包树。
6. Spring 管理、Jackson 序列化、FXML 注入、TestFX 或 GraalVM 需要反射的模块，应优先使用精确 `opens`。
7. 包名迁移必须同步 main/test import、资源路径、AOT/native metadata、文档与旧路径扫描。
8. 每补一类模块错误后立刻运行最小编译或测试命令验证。

## 模块名与主包根规则

- JPMS module name 默认取模块主要 Java 包根，例如 `io.geewit.utils.core.uuid`、`io.geewit.utils.javafx.controls`。
- 同一个 Java package 不应同时出现在多个 Gradle 模块的 `src/main/java` 中；发现 split package 时优先按模块职责拆包。
- 包名迁移应同时移动文件路径，保持 `src/main/java` 与 `package` 声明一致。
- 公共 API 或跨模块包名变更必须说明兼容性影响。

## 验证要求

按影响范围选择：

```bash
gradle :core:uuid:compileJava
gradle :core:jackson:test
gradle :data:spring:test
gradle :javafx:base:test
gradle :web:json:test
gradle publishToMavenLocal -Pio.geewit.publish.enabled=false
```

如果新增或修改 `module-info.java`，优先补最小描述符测试或至少运行目标模块 `compileJava`。

## 输出要求

- 涉及哪些模块。
- 是否改变 Java package、JPMS module name、exports / opens。
- 是否改变 Gradle 依赖方向。
- 是否影响公共 API 或 Maven 发布坐标。
- 执行了哪些验证命令。
