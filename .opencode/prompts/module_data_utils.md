# data 工具模块规则

适用模块：`data:commons`、`data:spring`。

规则：

- `data:commons` 保持轻量数据结构与通用数据工具，不依赖 Spring。
- `data:spring` 可以对接 Spring Data Commons，但不引入应用持久化实现。
- 不在工具库中引入 JPA/Hibernate 作为默认依赖。
- Criteria、Page、Sort、Converter 等工具要保持泛型边界清晰。
- 测试覆盖空集合、空分页、排序字段非法或缺失等场景。
