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
- 根 `build.gradle` 加载 `buildSrc/common-resolution-strategy.gradle`，统一 Java Library、Jacoco、toolchain、依赖排除与测试，并声明参与 Central 聚合发布的模块。
- `buildSrc/build.gradle` 与 `buildSrc/gradle.properties` 维护 buildSrc 自身构建配置。
- 依赖版本集中在 `gradle.properties` 与 `buildSrc/common-settings.gradle` 的 version catalog。

JReleaser 1.27.0 的上游实现仍调用 `Project.getProperties`，在 Gradle 9.8.2 下会产生弃用提示。升级 Gradle 10 前需要更新到已消除该调用的插件版本；仓库发布约定已使用 Provider API 和具有明确职责的 Gradle configurations。

## 发布现状

- 发布约定位于 `buildSrc/src/main/groovy`：子模块使用 `io.geewit.maven-publish`，根工程使用 `io.geewit.central-publish`。
- 发布链路使用 Gradle `maven-publish`、`signing` 和 JReleaser。各 publication 包含主 JAR、sources、Javadoc、POM 和 Gradle Module Metadata。
- Javadoc 使用 Java 25 的 `--no-fonts` 选项，通过系统字体展示文档，避免在每个模块中重复打包标准 Web 字体。
- 发布开关 `io.geewit.publish.enabled` 默认 `false`；本地发布始终可用。Central staging 与部署必须显式启用，并要求 release 版本与有效签名。
- 子模块 `gradle.properties` 中的 `group` / `artifactId` 是发布坐标真相源。
- 子模块每次 staging 发布前清理自己的 `build/staging-deploy`。根 `stageMavenCentral` 通过 Gradle artifact dependencies 收集当前版本到 `build/central-staging`，清除旧聚合文件。
- 根 `jreleaserDeploy` / `jreleaserFullRelease` 自动依赖 staging，以一个 Portal deployment 上传全部模块；不创建 Git tag 或 GitHub release。
- 子模块 `io.geewit.jreleaser.skip=true` 只跳过该模块独立 JReleaser 插件，不会将它排除在根聚合发布之外。设为 `false` 可使用该模块的独立部署入口。

### 凭据与签名

凭据放在用户级 `~/.gradle/gradle.properties` 或 CI 环境中，不写进仓库。Central 按以下顺序取值（同名 Gradle property 优先于环境变量）：

1. `SONATYPE_BEARER_TOKEN`
2. `SONATYPE_TOKEN`
3. `SONATYPE_USERNAME` + `SONATYPE_PASSWORD` 派生的 Base64 Bearer token；仍兼容 PASSWORD 已编码为 `username:password` 的旧配置。

签名支持 Gradle 现有的 `signing.keyId`、`signing.password`、`signing.secretKeyRingFile`。CI 也可注入 ASCII-armored 私钥 `SIGNING_KEY`、口令 `SIGNING_PASSWORD` 和可选子密钥 ID `SIGNING_KEY_ID`；对应 Gradle properties 为 `signingKey`、`signingPassword`、`signingKeyId`。

Gradle 完成制品签名，JReleaser 校验 POM、sources 和 Javadoc 后部署，Central 验证上传的签名。部署 ID 由 Central Portal 分配。若上传后连接中断，应先在 Portal 检查原 deployment 状态，再决定恢复或重试，避免重复上传不可覆盖的版本。

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
gradle -p buildSrc test
gradle publishToMavenLocal -Pio.geewit.publish.enabled=false
gradle stageMavenCentral -Pio.geewit.publish.enabled=true
```

正式发布前确认根 `version` 尚未发布，且 Central token 对各 artifact 的 namespace 有权限，签名公钥可由 Central 验证。执行：

```shell
gradle test
gradle jreleaserDeploy -Pio.geewit.publish.enabled=true
```

`stageMavenCentral` 只准备本地制品；`jreleaserDeploy` 会上传并发布到 Central。以 Portal 的 `PUBLISHED` 状态为完成依据，Central 下载镜像可能稍后可见。发布流程不执行 Git commit 或 push。
