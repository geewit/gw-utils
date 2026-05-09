---
name: javafx-spec
description: 在 gw-utils 仓库中实现或重构 JavaFX 工具、控件、Dialog、TableView、分页、复制、选择、打印预览或 JavaFX/Spring 集成时必须加载本技能。
compatibility: opencode
metadata:
  domain: javafx
  purpose: reusable-javafx-utilities-and-controls
---

# javafx-spec

## 适用场景

- `javafx:base`
- `javafx:controls`
- `javafx:spring`
- Dialog、TableView、Pagination、右键菜单、双击详情等通用控件能力
- JavaFX 与 Reactor / Spring 协作工具
- TestFX / headless 测试

## gw-utils 边界

1. JavaFX 模块只沉淀通用工具和控件，不写具体业务页面。
2. 不把业务 Controller、业务 ViewModel、业务 API client 放入工具库。
3. 平台兼容目标：Windows x64、macOS Intel、macOS Apple Silicon。
4. JavaFX 依赖版本来自 version catalog，不在模块中写死版本。

## 核心交互规则

可复用控件或工具若提供相关能力，应遵循：

1. Dialog 支持关闭 handler。
2. `确认/提交/保存` 在 `取消/关闭` 左边。
3. 保存、确认、提交成功后 Dialog 可由调用方配置自动关闭。
4. TableView 工具应支持分页、选择、复制、双击回调。
5. 删除类动作应提供二次确认扩展点，不直接绑定业务删除 API。
6. UI 更新必须发生在 JavaFX Application Thread。

## 线程规则

- 不在 JavaFX Application Thread 中执行文件 IO、网络调用、长耗时任务。
- 需要更新 UI 时使用 `Platform.runLater` 或明确的 JavaFX 调度封装。
- Reactor 订阅必须有错误处理，不吞异常。

## 测试规则

- 非 UI 逻辑优先普通单元测试。
- UI 测试保留 headless 参数。
- 不把测试写成只能在当前本机显示环境运行。

## 输出要求

- 属于 base / controls / spring 哪个模块。
- 是否影响公共控件 API。
- 如何保证 JavaFX 线程正确。
- 新增或修改了哪些测试。
