---
name: graalvm-spec
description: 在 gw-utils 仓库中处理 GraalVM native image、AOT、反射提示、Jackson 序列化、资源提示或 reachability metadata 时必须加载本技能。
compatibility: opencode
metadata:
  domain: native-image
  purpose: graalvm-and-aot-safety
---

# graalvm-spec

## 适用场景

- 修改 GraalVM native image 相关配置
- 修改 AOT hints / RuntimeHintsRegistrar
- 修改 Jackson 序列化/反序列化工具并需要 native 兼容
- 修改资源文件加载逻辑
- 修改反射、动态代理、ServiceLoader 使用方式

## gw-utils 边界

1. gw-utils 是工具库，native image 配置应尽量模块化、可选、低侵入。
2. 不为工具库强行引入应用级 native image 构建入口。
3. 反射提示应绑定具体工具能力，不做全局粗暴开放。
4. 公共 JSON/反射工具要考虑调用方 native image 场景。

## 核心规则

1. Jackson 反序列化需要无参构造、record 支持或显式 creator 时，要用测试验证。
2. 反射工具新增行为要说明 native image 下是否可达。
3. ServiceLoader 需要资源提示时，必须明确资源路径。
4. JavaFX 相关 native 配置必须考虑平台差异。
5. 不把调用方应用的所有类注册进 gw-utils。

## 推荐验证命令

```shell
gradle :core:jackson:test
gradle :core:reflection:test
gradle :javafx:base:test
```

如调用方项目有 nativeCompile，再在调用方执行 native 验证。

## 输出要求

- 是否新增/修改 RuntimeHints。
- 是否影响 JSON、反射、资源加载或 ServiceLoader。
- 是否需要调用方额外配置 native image。
- 执行或建议的验证命令。
