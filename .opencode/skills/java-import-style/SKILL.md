# java-import-style

## 目的

统一 Java 代码风格：优先使用 `import`，避免在代码中直接写带 package 的完整类名。

## 何时使用

当任务涉及以下任一情况时使用本 skill：

- 新增 Java 类
- 修改 Java 类
- 重构 Java 代码
- 修复 Java 编译问题
- 清理代码风格问题
- 整理 import / 命名冲突

## 规则

- 默认使用 `import` 引入类型
- 不要在普通代码逻辑中直接写 FQCN（fully qualified class name）
- 允许例外的情况：
    1. 同名类型冲突
    2. Javadoc、反射、配置字符串等必须保留全限定名
    3. 有明确可读性收益并说明原因
- 不使用无意义的通配符 import
- 修改后保持 import 整洁，无未使用 import

## 输出要求

在涉及 Java 文件修改时：

- 优先把新增类型引用写成 `import`
- 如保留 FQCN，必须说明原因
- 不要为风格问题做无关大范围改动