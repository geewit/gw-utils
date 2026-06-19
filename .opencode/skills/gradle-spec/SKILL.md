---
name: gradle-spec
description: 在 gw-utils 仓库中修改 Gradle、settings、buildSrc、依赖、插件、仓库、版本、打包、发布或 JReleaser 配置时必须先加载本技能。
compatibility: opencode
metadata:
  domain: build
  purpose: gradle-and-publish-safety
---

# gradle-spec

## 适用场景

- 修改 `settings.gradle`
- 修改根 `build.gradle`
- 修改模块 `build.gradle`
- 修改 `gradle.properties`
- 修改 `buildSrc/build.gradle`
- 修改 `buildSrc/gradle.properties`
- 修改 `buildSrc/**`
- 调整依赖、版本、仓库、插件
- 调整 Java toolchain、Jacoco、Javadoc、sourcesJar、publishing、signing、JReleaser

## 当前模块边界

```text
core:codec
core:date
core:enums
core:exceptions
core:jackson
core:lang
core:reflection
core:tree
core:uuid

data:commons
data:spring

i18n

javafx:base
javafx:controls
javafx:spring

web:converter
web:core
web:json
```

## 构建基线

- `settings.gradle` 只负责加载 `buildSrc/common-settings.gradle`、设置 `rootProject.name`、include 模块。
- `buildSrc/common-settings.gradle` 负责 pluginManagement、repository、version catalog。
- 根 `build.gradle` 负责统一 Java Library、Jacoco、toolchain、依赖排除、发布配置。
- `buildSrc/common-resolution-strategy.gradle` 负责依赖版本对齐。
- `buildSrc/build.gradle` 与 `buildSrc/gradle.properties` 负责 buildSrc 自身构建与 Gradle 执行参数。
- 子模块 `gradle.properties` 中的 `group` / `artifactId` 是发布坐标真相源。

## 发布链路硬约束

1. 必须保留 `maven-publish`。
2. 必须保留 `signing`。
3. 必须保留 `org.jreleaser`。
4. 必须保留 `io.geewit.publish.enabled` 发布开关。
5. 必须保留 JReleaser staging 仓库：`build/staging-deploy`。
6. 不得破坏 `POM_*` 元数据。
7. 不得随意改变已有 artifact 的 groupId / artifactId。
8. 发布凭据通过 Gradle properties / 环境注入，不写死真实 token、用户名、密码。
9. Central 凭据路径必须保留 `SONATYPE_BEARER_TOKEN` / `SONATYPE_TOKEN`，以及 `SONATYPE_USERNAME` + `SONATYPE_PASSWORD` 派生 Bearer token 的兼容逻辑。
10. 子模块可通过 `io.geewit.jreleaser.skip` 跳过 JReleaser 插件执行。

## 依赖方向

```text
core:*
  -> 外部基础库

core:jackson
  -> core:enums
  -> Jackson / Spring Boot 基础元数据

data:commons
  -> core:enums

data:spring
  -> Spring Data Commons

javafx:base
  -> JavaFX
  -> Reactor Core

javafx:controls
  -> JavaFX
  -> Ikonli

javafx:spring
  -> core:exceptions
  -> i18n
  -> javafx:base
  -> Spring / Spring Boot autoconfigure

web:converter
  -> core:enums
  -> Spring Context

web:json
  -> web:core
  -> Jackson
  -> Spring Web
```

## 核心规则

1. 统一使用 `gradle` 命令，不使用 `./gradlew`。
2. 变更前先检查：`settings.gradle`、根 `build.gradle`、`gradle.properties`、`buildSrc/build.gradle`、`buildSrc/gradle.properties`、`buildSrc/common-*`。
3. 依赖与版本尽量收敛到现有 version catalog，不随意新增平行版本源。
4. 新增依赖先查是否已有 `libs.*` alias。
5. 修改模块依赖时必须说明是否影响发布 artifact。
6. 工具库不引入 app/server 业务模块。
7. 不引入 Taro / miniapp / Node 前端构建链路。
8. 最小改动优先，不顺手重排整个构建系统。

## 常用验证命令

```shell
gradle projects
gradle :core:uuid:test
gradle :core:jackson:test
gradle :data:spring:test
gradle :javafx:base:test
gradle :web:json:test
gradle publishToMavenLocal -Pio.geewit.publish.enabled=false
```

发布链路变更时建议补充：

```shell
gradle publishAllPublicationsToJreleaserRepository -Pio.geewit.publish.enabled=true
gradle jreleaserConfig -Pio.geewit.publish.enabled=true
```

## 输出要求

- 改了哪些 Gradle / buildSrc / properties 文件。
- 为什么必须改。
- 是否改变模块依赖方向。
- 是否影响 groupId / artifactId / POM / JReleaser。
- 推荐的验证命令与结果。
