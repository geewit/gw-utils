# Desktop Export / Print / WebView Prompts

保留原 Codex prompt 中对 Excel 导出、PDF 打印、WebView 排版、JavaFX UI 交互一致性的关键约束。

具体规则已合并到 `.opencode/skills/javafx-spec/SKILL.md`：

- Excel 导出入口可以在 Controller 中触发，但列定义、数据组装、文件写入必须下沉到 service。
- PDF 打印流程必须分为：数据准备 -> HTML/模板排版 -> WebView/打印预览 -> 打印服务。
- WebView 仅用于预览和打印排版，禁止承载核心 CRUD 交互。
- Dialog、TableView、分页、双击详情、删除确认等交互规则以 `javafx-spec` 为准。
