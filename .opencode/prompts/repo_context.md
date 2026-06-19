# gw-utils 仓库上下文

## 仓库定位

`gw-utils` 是 Geewit Java 工具库集合，按 Gradle 多模块发布多个 Maven artifact。仓库需要保持可编译、可测试、可发布。

## Gradle 模块

以 `settings.gradle` 为准：

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

## 构建与发布

- 根 `settings.gradle` 加载 `buildSrc/common-settings.gradle`。
- 根 `build.gradle` 加载 `buildSrc/common-resolution-strategy.gradle`。
- `buildSrc/build.gradle` 与 `buildSrc/gradle.properties` 维护 buildSrc 自身构建。
- 版本统一来自 `gradle.properties` 与 version catalog。
- 每个子模块通过本模块 `gradle.properties` 定义 `group` 与 `artifactId`。
- 发布链路保留：`maven-publish` + `signing` + `org.jreleaser`。
- 发布开关：`io.geewit.publish.enabled=true|false`。
- JReleaser staging 仓库：`build/staging-deploy`。
- Central 凭据优先使用 `SONATYPE_BEARER_TOKEN` / `SONATYPE_TOKEN`，也支持由用户名和密码派生 Bearer token。
- 子模块可通过 `io.geewit.jreleaser.skip` 跳过 JReleaser 插件执行。

## 模块规则

- `core:*` 不依赖 `javafx:*`、`web:*`。
- `javafx:*` 可以依赖基础 `core:*` 与 `i18n`，但不能反向污染 core。
- `javafx:base` 当前使用 Reactor Core；修改 Mono / Flux / Scheduler 相关逻辑时必须加载 `reactor-spec`。
- `web:*` 只沉淀通用 Web/JSON 能力，不引入应用业务语义。
- 工具库不得引入 app/server 运行期概念。
- 依赖应优先使用 `libs.*` alias，不直接散落版本号。

## 测试规则

- 默认使用 JUnit Jupiter。
- 公共工具类优先覆盖边界条件、空值、非法参数、并发/线程安全场景。
- JavaFX 测试需要考虑 headless 参数与平台差异。
- 修改发布配置时至少验证 `gradle projects`、`gradle publishToMavenLocal -Pio.geewit.publish.enabled=false` 或对应模块 compile/test 命令。

## 协作方式

1. 先定位改动模块与入口。
2. 再判断是否影响公共 API、artifactId、groupId 或发布配置。
3. 修改 Gradle 时同步检查 version catalog 与 common resolution strategy。
4. 修改公共工具时补齐模块内测试。
5. 输出明确验证命令与结果。
