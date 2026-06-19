# gw-utils

集合多年经验，开源的 Java 核心工具类。

## 项目现状

`gw-utils` 是一个 Gradle 多模块 Java 工具库仓库，当前以 `settings.gradle` 为模块真相源，发布多个 Maven artifact。仓库不是 Spring Boot 应用工程，也不承载 app/server/persistence 业务架构。

当前模块：

- `:core:codec`
- `:core:date`
- `:core:enums`
- `:core:exceptions`
- `:core:jackson`
- `:core:lang`
- `:core:reflection`
- `:core:tree`
- `:core:uuid`
- `:data:commons`
- `:data:spring`
- `:i18n`
- `:javafx:base`
- `:javafx:controls`
- `:javafx:spring`
- `:web:converter`
- `:web:core`
- `:web:json`

## 构建基线

- Java 基线来自 `io.geewit.java.version=25`。
- 构建命令统一使用系统 `gradle`，当前按 Gradle 9.x 配置维护；不要使用 `./gradlew`。
- 根 `settings.gradle` 加载 `buildSrc/common-settings.gradle` 并 include 当前模块。
- 根 `build.gradle` 加载 `buildSrc/common-resolution-strategy.gradle`，统一 Java Library、Jacoco、toolchain、依赖排除、测试与发布配置。
- `buildSrc/build.gradle` 与 `buildSrc/gradle.properties` 维护 buildSrc 自身构建配置。
- 依赖版本集中在 `gradle.properties` 与 `buildSrc/common-settings.gradle` 的 version catalog。

## 发布现状

- 发布开关：`io.geewit.publish.enabled`。
- 发布链路保留 `maven-publish`、`signing`、`org.jreleaser`。
- 子模块 `gradle.properties` 中的 `group` / `artifactId` 是发布坐标真相源。
- JReleaser staging 仓库为各模块 `build/staging-deploy`。
- Central 凭据支持 `SONATYPE_BEARER_TOKEN` / `SONATYPE_TOKEN`，也可由 `SONATYPE_USERNAME` + `SONATYPE_PASSWORD` 派生 Bearer token。
- 各模块可通过 `io.geewit.jreleaser.skip` 控制是否应用 JReleaser 插件。

## 常用验证

```shell
gradle projects
gradle :core:uuid:test
gradle :core:jackson:test
gradle :data:spring:test
gradle :javafx:base:test
gradle :web:json:test
```

发布相关变更再补充：

```shell
gradle publishToMavenLocal -Pio.geewit.publish.enabled=false
gradle publishAllPublicationsToJreleaserRepository -Pio.geewit.publish.enabled=true
gradle jreleaserConfig -Pio.geewit.publish.enabled=true
```
