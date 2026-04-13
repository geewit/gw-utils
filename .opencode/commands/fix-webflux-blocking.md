---
description: 定位并修复 Spring WebFlux / Netty 链路中的阻塞调用风险
---

先读取并遵守以下文件：

1. `AGENTS.md`
2. `.opencode/README.md`
3. `.opencode/skills/spring-webflux-blocking-check/SKILL.md`

针对当前任务中的 WebFlux 阻塞问题执行处理，要求：

- 先定位阻塞点与调用链
- 判断是否处于 Netty event loop / WebFlux 主链路
- 说明为什么它是阻塞风险
- 选择最小改动修复方式
- 不要把 WebFlux 改成 WebMvc
- 不要通过无意义的 `subscribeOn` 或滥用 `boundedElastic` 掩盖问题
- 如果是 embedded 的 JDBC 调用，必须显式说明 blocking bridge 或 Scheduler 隔离边界
- 检查是否引入新的 reactive 反模式，例如：
    - `block()`
    - 无意义 `subscribe()`
    - `Mono<Void>` 上无效变换
    - 错误传播丢失

如果允许改代码，则实施最小修复；如果当前上下文是 review / 分析模式，则只输出修复方案。

最终输出格式：

## 阻塞点
- ...

## 风险分析
- ...

## 修复方案
- ...

## 改动文件
- ...

## 阻塞隔离边界说明
- ...

## 验证命令
```bash
...
```
