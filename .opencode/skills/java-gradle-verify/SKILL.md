---
name: java-gradle-verify
description: Verify Java/Gradle changes in a multi-module repository using the project's required environment and validation flow.
---

# java-gradle-verify

用于在多模块 Java / Gradle 仓库中执行统一的验证流程。

## 适用场景

当完成以下任一工作后使用：

- Java 代码改动
- 配置改动
- repository / service / controller / handler 改动
- JavaFX 页面或控制器改动
- Embedded / Server 模块改动
- PR 前自检

## 固定环境

执行命令前，优先显式设置：

```bash
export JAVA_HOME=/Users/geewit/.sdkman/candidates/java/25.0.2-graal
export GRADLE_HOME=/Users/geewit/.sdkman/candidates/gradle/current
export PATH="$JAVA_HOME/bin:$GRADLE_HOME/bin:$PATH"
```

## 命令约束

- 禁止使用 `./gradlew`
- 必须直接使用 `gradle`

## 默认验证顺序

### 1. 基础环境检查

```bash
java -version
gradle -version
```

### 2. 项目结构检查

```bash
gradle projects
```

### 3. 最小必要验证

优先执行与本次改动最相关的验证：

- 单模块改动：优先跑对应模块测试
- 跨模块改动：至少跑受影响模块测试
- 公共模块改动：评估是否需要扩大到更多模块验证

示例：

```bash
gradle :app:test
gradle :embedded:test
gradle :server:edge:test
gradle :server:cloud:test
```

### 4. 编译验证

按需执行：

```bash
gradle :app:compileJava
gradle :embedded:compileJava
gradle :server:edge:compileJava
gradle :server:cloud:compileJava
```

### 5. 构建验证

在改动影响面较大时执行：

```bash
gradle clean build
```

### 6. 补充检查

如仓库存在额外检查，则一并执行：

- 格式检查
- 静态检查
- 自定义 verify 脚本
- 安全检查

例如：

```bash
bash ./.opencode/shell/verify-embedded.sh
```

## 验证策略

### 小改动
- 相关模块 `test`
- 必要时补 `compileJava`

### 中等改动
- 相关模块 `test`
- 相关模块 `compileJava`
- 受影响公共模块联动验证

### 大改动 / 公共模块改动
- 优先 `gradle clean build`
- 如耗时过大，至少明确说明未执行的部分与原因

## 输出要求

输出必须包含：

- 执行了哪些验证命令
- 哪些命令通过
- 哪些命令未执行
- 哪些命令失败
- 失败的关键原因
- 对最终可交付性的判断

## 注意事项

- 不要只说“建议运行测试”，要明确给出命令
- 不要在未执行时假装通过
- 如因环境、依赖或仓库状态限制无法执行，要诚实说明
- 如只执行了局部验证，要说明覆盖范围与剩余风险