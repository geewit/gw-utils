# JavaFX 工具模块规则

适用模块：`javafx:base`、`javafx:controls`、`javafx:spring`。

规则：

- JavaFX UI 更新必须发生在 JavaFX Application Thread。
- 公共控件工具应保持平台兼容：Windows x64、macOS Intel、macOS Apple Silicon。
- 不把业务应用的 Controller / ViewModel 语义写入工具库。
- Dialog、Table、分页、复制、选择等通用能力要沉淀为可复用 API。
- 涉及测试时优先覆盖非 UI 逻辑；UI 测试保留 headless 参数。
