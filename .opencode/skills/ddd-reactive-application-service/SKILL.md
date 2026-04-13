# Skill: DDD Reactive Application Service

## 适用场景

当任务涉及：
- Spring WebFlux / Reactor 链路中的应用服务设计
- 将 handler/controller 里的业务规则下沉
- 设计响应式编排与领域调用边界

## 强制规则

1. `Mono` / `Flux` 只属于应用层或接口层编排，不属于领域模型本身。
2. 应用服务负责：参数校验、权限校验、加载聚合、调用领域规则、持久化、发布事件、组装返回。
3. 不要在一个 Reactor 链里混杂过多语义；权限、领域决策、远程调用、持久化、映射要清晰分段。
4. 阻塞资源必须通过专用 Scheduler / blocking bridge 隔离。
5. 错误类型要尽量表达业务语义，不要全部退化为技术异常。

## 典型分层

- `interfaces.*`：协议入参、响应映射
- `application.*`：响应式编排
- `domain.*`：同步语义的业务决策
- `infrastructure.*`：R2DBC/JDBC/Netty/client 实现
