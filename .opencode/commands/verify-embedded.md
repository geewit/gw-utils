---
description: 校验 embedded 模块是否仍符合 WebFlux + SQLite + JDBC 的架构约束
---

先读取并遵守以下文件：

1. `AGENTS.md`
2. `.opencode/rules/ddd-package-map.md`
3. `.opencode/README.md`
4. `.opencode/prompts/*.md`

执行一次 embedded 模块专项校验，目标：

- 确认 embedded 仍保持 WebFlux / Reactive WebSocket 主链路
- 确认本地持久化为 SQLite + JDBC
- 确认 JDBC 没有直接跑在 event loop 上
- 确认没有误引入本地 MySQL / R2DBC 强依赖
- 确认文档、脚本、配置与当前实现一致
- 确认没有错误使用 `./gradlew`

优先给出并按需执行以下验证命令：

```bash
export JAVA_HOME=/Users/geewit/.sdkman/candidates/java/25.0.2-graal
export GRADLE_HOME=/Users/geewit/.sdkman/candidates/gradle/current
export PATH="$JAVA_HOME/bin:$GRADLE_HOME/bin:$PATH"

gradle :embedded:compileJava
gradle :embedded:test
bash ./.opencode/shell/verify-embedded.sh
```

最终输出格式：

## 架构一致性结论
- ...

## WebFlux / WebSocket 检查
- ...

## SQLite / JDBC 检查
- ...

## 阻塞隔离检查
- ...

## 文档 / 脚本 / 配置同步情况
- ...

## 发现的问题
1. ...
2. ...

## 建议动作
1. ...
2. ...
