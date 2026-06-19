---
name: java-spec
description: 在 gw-utils 仓库中处理 Java 源码通用重构、JDK/Apache Commons/Guava/Spring 工具方法取舍、空值/字符串/集合/对象默认值规范、全限定类型名与 import 清理、重复功能避免、小范围等价替换时必须加载本技能。
compatibility: opencode
metadata:
  domain: java
  purpose: source-style-and-utility-method-safety
---

# java-spec

## 适用场景

- 修改 Java 源码但不属于更具体的 JavaFX、Spring Data、Reactor、GraalVM、ID/UUID 场景。
- 将手写空值、blank、trim、默认值、大小写匹配、集合判空、枚举解析、数字解析、摘要计算等 helper 替换为现有 JDK / Apache Commons / Guava / Spring Framework 方法。
- 清理类型声明、泛型、变量、参数、返回值、异常声明等位置的冗余全限定类型名。
- 清理因重构产生的私有工具方法、反射测试和未使用 import。

## 核心规则

1. 新增或重构方法前，必须先在当前类、当前模块、项目代码和当前模块已引入依赖库 API 范围内查找是否已有相同功能。
2. 发现相同代码块或多个调用点存在等价实现时，优先在最小合理语义边界内抽取公共方法并复用。
3. 优先复用当前模块已经可用的 JDK / Apache Commons / Guava / Spring Framework 工具方法，不为局部便利新增依赖。
4. 不新增只包装通用库语义的方法，例如 `nullToEmpty`、`trimToNull`、`isBlank`、`defaultIfBlank`、`firstNonBlank`、`parseEnum`、`parseIntOrDefault`、`sha256`、`md5Hex`。
5. helper 名称承载业务或协议语义时可以保留语义方法，但方法内部仍应优先调用通用库完成基础处理。
6. 替换前必须确认等价性，特别注意 trim、大小写、字符集、异常类型和 null 行为。
7. 删除私有 helper 后，同步删除只反射测试该 helper 的测试，改为覆盖真实调用路径。
8. 空值保护必须覆盖同一变量的全部后续使用，不用 `Objects.requireNonNull` 压制合法 null 分支。
9. Stream 只需要最小元素时用 `min(comparator)` 替代 `sorted(comparator).findFirst()`。
10. 系统调用和 IO 副作用返回值不能随手丢弃，例如 `File#setExecutable` 的 boolean 结果要检查并抛错或记录。
11. Java 源码正文里的类型引用优先通过 import 引入短类名；字符串形式类名、配置 key、日志名、短类名冲突可保留全包名。
12. 新增或重构代码时尽量不要调用已废弃 API；确需保留时把 `@SuppressWarnings("deprecation")` 放在最窄作用域并说明理由。

## 常用工具方法偏好

- Apache Commons Lang / Collections 已可用时，优先使用 `StringUtils`、`ObjectUtils`、`BooleanUtils`、`NumberUtils`、`EnumUtils`、`ArrayUtils`、`CollectionUtils`。
- Guava 已可用时，可用 `Hashing`、`MoreFiles`、`Strings`，但同一文件避免 Commons / Guava 混用导致可读性下降。
- Spring Framework 已可用时，可使用 `org.springframework.util.StringUtils`、`CollectionUtils`、`ObjectUtils`、`DigestUtils`，并保持文件既有 Spring 风格。
- JDK 已覆盖时优先用 JDK：`Objects.equals`、`Objects.requireNonNullElse`、`List.copyOf`、`Map.copyOf`、`HexFormat`、`Optional` 的真实业务语义方法。

## 验证要求

- 先用 `rg` 扫描被替换的 helper 名称、关键语义词和相同代码片段。
- 删除派生字段、getter/setter 或私有 helper 后，用 `rg` 查测试是否还在反射或字段名字符串中断言旧内部结构。
- 清理全包名类型引用后，用 `rg --pcre2 "^(?!\\s*(package|import)\\b).*\\b(?:java|javax|javafx|org|com|io|reactor|tools)\\.[A-Za-z0-9_.]*\\.[A-Z][A-Za-z0-9_]*"` 等扫描源码正文；保留项需在输出中说明原因。
- 至少运行受影响模块的 `compileJava` 或最小相关测试。

## 输出要求

- 说明是否新增、删除或替换了公共 API。
- 说明复用了哪些现有工具方法或为什么没有复用。
- 说明验证命令与真实结果。
