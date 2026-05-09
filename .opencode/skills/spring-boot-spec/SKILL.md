---
name: spring-boot-spec
description: 在 gw-utils 仓库中处理 Spring、Spring Boot、Spring Data Commons、自动配置、配置属性或模块级 Spring 装配时必须加载本技能。
compatibility: opencode
metadata:
  domain: spring
  purpose: spring-utility-module-structure
---

# spring-boot-spec

## 适用场景

- `data:spring` 中的 Spring Data Commons 适配
- `javafx:spring` 中的 JavaFX + Spring 集成工具
- Spring Bean / `@Configuration` / `@ConfigurationProperties`
- `AutoConfiguration.imports`
- GraalVM native / AOT hints 相关 Spring 接线

## gw-utils 边界

1. gw-utils 是工具库，不是 Spring Boot 应用。
2. 不新增 app/server 运行入口。
3. 不在工具库里定义业务 Controller、业务 Handler、业务 Security 主链路。
4. Spring 相关模块应保持可选、轻量、可复用。
5. 不引入 JPA/Hibernate 作为默认依赖。

## 核心规则

1. Spring Bean 命名、条件装配、配置属性类必须可测试、可覆盖、可追踪。
2. 自动配置必须尽量使用条件注解，避免强制污染调用方 ApplicationContext。
3. 不启用 bean definition overriding 作为解决冲突的手段。
4. `data:commons` 不依赖 Spring；Spring 适配放入 `data:spring`。
5. JavaFX + Spring 集成能力放入 `javafx:spring`，不要反向写入 core。
6. AOT/native hints 必须和对应模块能力绑定，不做全局粗暴开放。

## 推荐检查清单

- 新增 Bean 是否处于正确模块。
- 是否把应用业务语义写进工具库。
- 是否新增了不必要的 starter 依赖。
- 是否影响已有公共 API。
- 是否需要 AOT/runtime hints。

## 必查文件

- `AGENTS.md`
- `.opencode/prompts/repo_context.md`
- `.opencode/prompts/module_data_utils.md`
- `.opencode/prompts/module_javafx_utils.md`
- `.opencode/skills/architecture-spec/SKILL.md`

## 输出要求

- 标明改动属于 Spring 工具、自动配置、配置属性还是测试。
- 标明是否影响公共 API 或调用方 ApplicationContext。
- 给出最小验证命令，例如 `gradle :data:spring:test`、`gradle :javafx:spring:test`。
