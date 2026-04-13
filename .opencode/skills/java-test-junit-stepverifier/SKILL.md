---
name: java-test-junit-stepverifier
description: Add or update Java tests using JUnit 6, StepVerifier, and project-consistent testing strategy.
---

# java-test-junit-stepverifier

用于为 Java / Spring / Reactor 代码补充或修改测试。

## 适用场景

- 新增功能补测试
- bugfix 补回归测试
- WebFlux service / repository / handler 测试
- JavaFX service 逻辑测试
- 现有测试不完整或风格不一致

## 默认测试策略

- 单元测试：JUnit 6
- Reactive 逻辑：StepVerifier
- Mock：Mockito 或项目既有 mock 方式
- WebFlux 接口：按需使用 WebTestClient
- UI 层：优先测 service 和 controller 的非 UI 逻辑部分

## 覆盖要求

每个新增功能至少覆盖：

- 正常路径
- 一个边界路径或异常路径

必要时增加：

- 空值 / 空结果
- 非法输入
- 重复数据
- 超时 / 错误传播
- 分支条件

## 编写步骤

### 1. 先识别被测对象类型

区分：

- 纯工具类
- service
- controller / handler
- repository
- converter / mapper
- JavaFX controller 中的可测试逻辑

### 2. 按类型选择测试方式

#### 普通 Java 类
- 直接 JUnit

#### Reactive service / repository
- StepVerifier

#### WebFlux 接口
- WebTestClient

#### JavaFX
- 优先测非 UI 纯逻辑
- 尽量避免高成本 UI 自动化

### 3. 断言重点

优先断言：

- 业务行为
- 状态变化
- 输出结果
- 错误传播
- 关键副作用

避免：

- 过度依赖实现细节
- 对无意义日志、私有实现细节做脆弱断言

### 4. 测试命名

建议体现：

- 被测方法
- 场景
- 预期结果

例如：

- `shouldReturnConfigWhenIdExists`
- `shouldEmitErrorWhenRequestInvalid`
- `shouldCloseDialogAfterSaveSuccess`

### 5. 保持最小必要 mock

要求：

- 能不 mock 的尽量不 mock
- mock 只覆盖外部依赖
- 不要把测试写成实现复制品

## 输出要求

最终输出应包含：

- 新增或修改的测试文件
- 覆盖了哪些场景
- 哪些场景仍未覆盖
- 验证命令

## 禁止事项

- 不要只补 happy path
- 不要为了凑覆盖率写无意义断言
- 不要把 reactive 逻辑硬改成同步测试
- 不要宣称测试通过但未执行