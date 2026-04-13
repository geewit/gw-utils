---
name: javafx-ui-safe-change
description: Safely implement or modify JavaFX UI, controllers, dialogs, and background interactions without blocking the UI thread.
---

# javafx-ui-safe-change

用于在 JavaFX 模块中安全地实现或修改页面、控制器、dialog、后台交互逻辑。

## 适用场景

- 修改 FXML Controller
- 新增或修改 dialog
- 页面按钮、表单、列表、图标改造
- JavaFX 与 Reactor / service 协作逻辑改造
- 修复 UI 线程阻塞问题

## 默认约束

- UI 更新仅允许发生在 JavaFX Application Thread
- 网络 IO、数据库 IO、阻塞逻辑不得运行在 UI 线程
- 复用现有 controller / service / client 结构
- dialog 必须具备关闭事件处理
- dialog 保存成功后应关闭 dialog
- 优先最小改动

## 实施步骤

### 1. 扫描现状

先查：

- 对应 FXML
- Controller 类
- service / client 调用入口
- 图标、样式、资源目录
- 现有 dialog 实现风格
- JavaFX 与 Reactor 的协作方式

### 2. 识别 UI 与后台职责边界

明确：

- 哪些逻辑只负责 UI 展示
- 哪些逻辑属于 service / client
- 哪些操作可能阻塞
- 哪些更新必须切回 JavaFX 线程执行

### 3. 保持 UI 线程安全

要求：

- 不在按钮事件里直接做数据库或网络调用
- 不在 UI 线程执行耗时计算
- 背景执行后，UI 更新回到 JavaFX 线程
- 避免页面初始化阶段执行阻塞 IO

### 4. Dialog 规则

所有 dialog 必须：

- 有明确关闭处理
- 保存成功后关闭 dialog
- 异常时给出用户可理解提示
- 不把保存逻辑与 UI 更新混成不可维护代码

### 5. 图标 / 样式改造

要求：

- 优先复用现有图标体系
- 保持样式命名一致
- 不无理由重构整个页面布局
- 仅对任务要求的按钮、控件、页面做最小必要修改

### 6. Reactor / 后台线程协作

如果页面使用 Reactor：

- 后台 IO 在合适 Scheduler 上执行
- UI 更新切回 JavaFX 线程
- 不要无意义 `subscribe`
- 错误处理要回传 UI 层

## 测试建议

优先测试：

- service 逻辑
- controller 的非 UI 纯逻辑部分
- 参数校验
- 保存 / 关闭流程

## 输出要求

最终输出应包含：

- 修改的 FXML / Controller / service / resource
- UI 线程安全说明
- dialog 关闭行为说明
- 是否新增图标资源或字体图标引用
- 验证方式

## 禁止事项

- 不要在 UI 线程做阻塞 IO
- 不要让 dialog 保存成功后仍停留不关闭
- 不要把网络、数据库逻辑直接写在按钮点击事件中
- 不要无关大改页面结构