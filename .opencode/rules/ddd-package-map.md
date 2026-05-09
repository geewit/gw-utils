# gw-utils Package Map

- `core:*`：基础工具包，不承载业务用例。
- `data:commons`：轻量数据对象与通用数据结构。
- `data:spring`：Spring Data Commons 适配。
- `i18n`：国际化工具。
- `javafx:*`：JavaFX 可复用工具、控件、Spring 集成。
- `web:*`：Web / JSON 通用工具。

工具库不默认采用应用 DDD 分层；只有当某个模块内部确实出现复杂规则时，才局部使用清晰包结构，避免过度设计。
