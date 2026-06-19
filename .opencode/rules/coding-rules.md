# gw-utils 编码规则

## 基础约束

1. 遵循现有项目风格、包结构、命名风格。
2. 优先最小改动。
3. 不引入应用级 app/server 业务语义。
4. 不破坏已有公共 API，确需破坏时必须说明兼容性影响。
5. 不引入 ORM 作为工具库默认依赖。
6. 新增依赖必须先确认是否已有 `libs.*` alias。

## 模块依赖约束

1. `core:*` 不依赖 `javafx:*`、`web:*`、`data:spring`。
2. `data:commons` 不依赖 Spring。
3. `data:spring` 可依赖 Spring Data Commons，但不承载具体业务 repository。
4. `javafx:*` 不反向污染 `core:*`。
5. `web:*` 不引入具体业务接口路径或应用鉴权主链路。

## 公共 API 约束

1. 工具库 API 优先保持源码兼容和二进制兼容。
2. 能新增重载，不随意删除旧方法。
3. 参数校验策略必须稳定：null、空字符串、空集合、非法格式要有明确行为。
4. 异常类型必须可读、可定位，不吞异常。

## Gradle 与发布约束

1. 统一使用 `gradle`，禁止使用 `./gradlew`。
2. 修改 Gradle 前必须检查：`settings.gradle`、根 `build.gradle`、`gradle.properties`、`buildSrc/**`。
3. 保留 `maven-publish`、`signing`、`org.jreleaser`。
4. 保留 `io.geewit.publish.enabled` 发布开关。
5. 子模块 `gradle.properties` 中的 `group` / `artifactId` 是发布坐标真相源。
6. 版本尽量放入 `gradle.properties` 与 version catalog。

## JavaFX 约束

1. UI 更新必须在 JavaFX Application Thread。
2. 通用 JavaFX 控件不得携带具体业务应用语义。
3. 平台相关依赖必须考虑 Windows x64、macOS Intel、macOS Apple Silicon。
4. 测试时保留 headless 参数，不把测试写成只适配本机显示环境。

## JSON / Jackson 约束

1. 序列化字段名变更必须说明兼容性影响。
2. 新增 JSON 行为要覆盖序列化与反序列化测试。
3. 不为局部便利散落多个 ObjectMapper 配置风格。

## 测试约束

1. 新增行为必须补最小必要测试。
2. 修复 bug 必须补能复现 bug 的测试。
3. 公共工具类测试必须覆盖边界条件。
4. 不为测试方便破坏公共 API。

## Code Quality Loop

- 修改 Java 代码后必须至少运行相关模块测试；收尾前按风险扩大到 `gradle test`。
- 如果 `.codex/code-quality/summary.md` 存在，修复代码质量问题前必须优先读取该 summary、完整 log 与 reports 清单。
- 修复顺序：编译/测试失败、OpenRewrite patch、Error Prone / NullAway、SpotBugs、PMD、Checkstyle、ArchUnit。
- 不要为了消除告警随意添加 `@SuppressWarnings` 或 `// noinspection`；只有明确误报才允许抑制并说明原因。
- gw-utils 当前没有默认质量扫描脚本时，不要把不存在的 `scripts/quality-scan.sh` 写成必跑入口。
- 质量扫描命令如后续接入，必须统一使用 `gradle`，不要使用 Gradle wrapper 命令。

## Java 清理与现代化规则

### 移除未使用元素

- 及时删除未使用的 import、字段、构造函数参数和方法。
- 测试类中及时删除未使用的 mock 对象和 stub。

### 优先使用现代 Java API

- 优先使用清晰的 JDK / Apache Commons / Guava / Spring 工具方法替代重复 helper。
- Java 25 基线下可以使用当前 JDK 已稳定提供的 API，但不要为了新语法牺牲兼容性或可读性。
- 使用类型导入替代正文中的完全限定名；短类名冲突、配置 key、字符串类名可保留全名。
- Stream 只需要最小元素时用 `min(comparator)` 替代 `sorted(comparator).findFirst()`。

### 构造函数与 setter 规范

- 构造函数和 setter 中对同名字段赋值时使用显式 `this.`。
- 空值保护要覆盖变量的全部后续使用，不用非空断言掩盖合法分支。
