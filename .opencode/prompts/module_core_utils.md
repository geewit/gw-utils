# core 工具模块规则

适用模块：`core:codec`、`core:date`、`core:enums`、`core:exceptions`、`core:jackson`、`core:lang`、`core:reflection`、`core:tree`、`core:uuid`。

规则：

- 保持工具库语义纯净，不引入业务模型。
- 公共 API 变更必须考虑二进制兼容与源码兼容。
- 不从 `core:*` 依赖 `javafx:*`、`web:*` 或应用运行时模块。
- 异常类型、枚举、UUID、Jackson 工具要补边界测试。
- 日期/时间工具避免隐式依赖系统默认时区，必要时显式传入 `ZoneId` / `Clock`。
