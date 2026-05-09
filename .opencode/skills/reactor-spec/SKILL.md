---
name: reactor-spec
description: 在 gw-utils 仓库中处理 Reactor Core、Mono、Flux、Scheduler、响应式工具或阻塞隔离封装时必须加载本技能。
compatibility: opencode
metadata:
  domain: reactive
  purpose: reactor-utility-and-blocking-boundary
---

# reactor-spec

## 适用场景

- `Mono` / `Flux` 工具封装
- Scheduler / 线程切换封装
- JavaFX 与 Reactor 协作工具
- 同步任务包装为响应式 API
- StepVerifier 测试

## gw-utils 边界

1. gw-utils 当前不是 WebFlux 服务端仓库。
2. 不在工具库中引入具体业务 WSS 协议。
3. Reactor 工具必须保持通用，不绑定 app/server 业务语义。
4. 阻塞调用封装必须清晰暴露 Scheduler/Executor 边界。

## 核心规则

1. 不在调用方 event loop / UI 线程上执行阻塞调用。
2. Reactor 类型不要污染纯领域/值对象模型。
3. 不裸 `subscribe()` 吞异常；工具方法应返回 `Mono` / `Flux` 让调用方组合。
4. 错误必须保留上下文，不全部退化为裸 `RuntimeException`。
5. 测试优先使用 StepVerifier。

## Mono<Void> 与 switchIfEmpty 互斥分支模式

`Mono<Void>` 完成信号本身就是 empty，不适合直接承载 `switchIfEmpty` 分支判断。需要互斥分支时，中间链返回非 Void 值，最后再 `.then()`。

```java
return repository.findById(id)
    .flatMap(existing -> update(existing).thenReturn(Boolean.TRUE))
    .switchIfEmpty(Mono.defer(() -> insert().thenReturn(Boolean.TRUE)))
    .then();
```

## 输出要求

- 哪些部分是响应式编排。
- 阻塞点在哪里，如何隔离。
- 是否影响 JavaFX UI 线程或调用方线程模型。
- 新增或修改了哪些响应式测试。
