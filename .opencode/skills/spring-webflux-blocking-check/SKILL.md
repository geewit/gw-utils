---
name: spring-webflux-blocking-check
description: Detect and fix blocking-call risks in Spring WebFlux and Netty event-loop based code paths.
---

# spring-webflux-blocking-check

用于检查并修复 Spring WebFlux / Netty 事件循环中的阻塞调用风险。

## 适用场景

当任务涉及以下任一情况时使用：

- WebFlux controller / handler / filter / service 改动
- Reactive WebSocket handler 改动
- Embedded / server 模块中接入 JDBC、文件 IO、外部 SDK、阻塞 API
- IDE 或静态检查提示：
    - Possibly blocking call in non-blocking context
    - BlockingMethodInNonBlockingContext
    - event-loop 阻塞风险
- 代码中出现 `block()`、`Thread.sleep()`、同步 JDBC、文件操作、同步 HTTP 调用等

## 核心目标

1. 找出阻塞点
2. 判断阻塞点是否位于 WebFlux / Netty event loop 链路
3. 给出最小改动修复方案
4. 保持 WebFlux 主链路不被破坏

## 检查重点

### 高风险阻塞来源

重点排查：

- JDBC 调用
- `CrudRepository` / `JdbcTemplate` / `DataSource`
- 文件 IO
- `Thread.sleep`
- 同步锁长时间占用
- 同步 HTTP 客户端
- `Future#get`
- `CompletableFuture#join`
- `Mono#block`
- `Flux#toStream`
- 大量 CPU 密集逻辑直接运行在 reactive 链路中
- 第三方 SDK 的同步阻塞调用

### 高风险位置

重点查看：

- `@RestController`
- WebFlux handler
- `WebFilter`
- `WebSocketHandler`
- `service` 中被 reactive 链路直接调用的方法
- `flatMap` / `map` / `doOnNext` / `handle` 中执行的同步阻塞逻辑

## 处理原则

### 原则 1：不要在 event loop 上直接做阻塞调用

禁止：

- 在 Netty event loop 线程执行 JDBC
- 在 `map` / `flatMap` 里直接跑阻塞文件 IO
- 在 WebSocket 消息处理线程上做同步数据库调用

### 原则 2：阻塞逻辑必须显式隔离

可用方案：

- `Mono.fromCallable(...)`
- `Mono.fromRunnable(...)`
- `Flux.defer(...)`
- `subscribeOn(Schedulers.boundedElastic())`
- 项目已有自定义 Scheduler
- 专用 blocking bridge

### 原则 3：调用边界必须清晰可读

推荐把阻塞代码封装在边界明确的位置，例如：

- repository adapter
- blocking bridge service
- dedicated executor wrapper

不要把阻塞隔离散落在业务链路各处。

## 修复流程

### 1. 识别调用链

先回答：

- 当前阻塞调用是什么
- 它从哪里被调用
- 是否处于 WebFlux 主链路
- 是否可能运行在 event loop

### 2. 判断影响范围

判断：

- 是否只影响单点
- 是否是公共 service / 公共 repository
- 是否会影响高频请求
- 是否影响 WebSocket 长连接处理

### 3. 选择最小改动方案

常见修复方式：

#### JDBC / 同步 repository
使用 `Mono.fromCallable` 或 `Mono.fromRunnable` 包装，并切换到 blocking Scheduler。

#### 文件 IO
隔离到 `boundedElastic` 或项目专用线程池。

#### 第三方同步 SDK
封装成 blocking bridge，再由 reactive service 调用。

#### CPU 密集逻辑
视情况考虑专用 Scheduler，不要长时间占用 event loop。

### 4. 复查 reactive 语义

检查是否引入以下问题：

- 重复订阅
- 不必要的 `subscribe`
- 丢失错误处理
- `Mono<Void>` 上做无效变换
- 在 `doOn...` 中误放业务逻辑

## 输出要求

输出必须包含：

- 阻塞点位置
- 为什么它有风险
- 它是否位于 event loop 链路
- 修复方案
- 修改后的调用边界说明
- 验证方式

## 最终说明必须解释

- 阻塞逻辑被隔离到了哪里
- 为什么该方案符合当前项目架构
- 是否仍有剩余风险

## 禁止事项

- 不要为了去掉警告而把整个 WebFlux 改成 WebMvc
- 不要把阻塞逻辑偷偷藏进 `map` / `flatMap`
- 不要无理由到处添加 `boundedElastic`
- 不要在没有说明边界的情况下引入阻塞桥接