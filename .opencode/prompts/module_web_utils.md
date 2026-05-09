# web / json 工具模块规则

适用模块：`web:converter`、`web:core`、`web:json`。

规则：

- 保持通用 Web/JSON 支持，不引入具体业务接口路径。
- JSON 字段命名、序列化行为要保持向后兼容。
- Converter 应覆盖 null、空字符串、非法格式、边界格式。
- `web:json` 可以依赖 Jackson，但不引入完整应用级 WebFlux/Spring Security 主链路。
