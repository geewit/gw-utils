---
name: java-gradle-safe-change
description: Safely perform incremental changes in a multi-module Java Gradle repository with strict project constraints.
---

# java-gradle-safe-change

用于在多模块 Java / Gradle 仓库中执行安全的增量改造。

## 适用场景

当任务涉及以下任一情况时使用：

- 需要修改 Java 代码
- 需要跨模块定位实现入口
- 需要在不破坏现有结构的前提下做最小改动
- 任务描述可能与仓库现状不完全一致
- 仓库对 Gradle、buildSrc、公共 API 有严格边界要求

## 仓库默认约束

执行本 skill 时，默认遵守以下约束：

- 先扫描仓库现状，再做修改
- 只做任务范围内的增量改造
- 不基于猜测修改代码
- 不回滚上一版已完成且仍有效的改动
- 不做无关大重构
- 不修改 `buildSrc`
- 不修改现有 Gradle 脚本，除非任务明确要求
- 不擅自重命名公共 API
- 构建、测试、检查统一使用 `gradle`
- 禁止使用 `./gradlew`

## 固定环境

执行命令时，优先显式设置：

```bash
export JAVA_HOME=/Users/geewit/.sdkman/candidates/java/25.0.2-graal
export GRADLE_HOME=/Users/geewit/.sdkman/candidates/gradle/current
export PATH="$JAVA_HOME/bin:$GRADLE_HOME/bin:$PATH"
```

## 工作步骤

### 1. 先扫描仓库

至少完成以下检查：

1. 识别项目是否为多模块 Gradle 仓库
2. 定位本次任务涉及的模块
3. 查找入口类、配置类、controller、handler、service、repository、test
4. 查找是否已有相似实现
5. 检查当前任务是否与已有规则冲突
6. 检查是否涉及公共 API、模块依赖或配置格式变更

### 2. 输出扫描结论

在动手修改前，先明确：

- 当前仓库真实状态
- 与任务描述可能存在的差异
- 推荐的最小改动点
- 潜在影响模块
- 预期验证方式

### 3. 实施最小改动

要求：

- 仅修改完成任务所需文件
- 尽量保持现有包结构、命名风格、分层方式
- 优先复用已有实现
- 不引入与任务无关的重构
- 不扩大 API 变更面

### 4. 验证改动

至少考虑：

- 能否编译
- 是否有对应测试需要补齐或调整
- 是否影响其他模块
- 是否破坏项目原有约束

### 5. 输出最终结果

最终输出必须包含：

- 改动文件列表
- 改动说明
- 验证命令
- 验证结果
- 风险与兼容性说明

## 输出风格要求

- 明确说明“先扫描后修改”
- 明确区分“仓库现状”与“任务假设”
- 如有不确定项，必须显式说明假设
- 不要在未确认仓库真实状态前直接大改

## 禁止事项

- 不要直接从任务描述推断文件路径并盲改
- 不要在未检查引用关系前改公共 DTO / config / constants
- 不要把一次局部改动扩大成全仓重构
- 不要使用 `./gradlew`
- 不要修改 `buildSrc`