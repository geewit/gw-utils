---
name: security-config-scan-java
description: Perform a focused security and configuration scan for Java/Spring repository changes before delivery.
---

# security-config-scan-java

用于在 Java / Spring 仓库中做提交前的安全与配置检查。

## 适用场景

- 提交前自检
- PR review 前检查
- 修改配置文件后检查
- 修改鉴权、日志、数据库访问代码后检查
- 涉及 WebFlux / WebSocket / JDBC / R2DBC 改动时检查

## 检查目标

1. 防止敏感信息泄漏
2. 防止不安全配置进入仓库
3. 防止参数化缺失带来的注入风险
4. 防止异常与日志泄漏内部细节
5. 确保 REST / WebSocket 鉴权策略一致

## 检查步骤

### 1. 配置文件检查

重点检查：

- `application.properties`
- `application.yml`
- `env/*`
- `.properties`
- `.yaml`
- `.yml`

确认：

- 未写入真实密钥、token、数据库密码
- 优先使用环境变量
- 配置名称与现有风格一致
- 没有把本地临时调试配置误提交

### 2. 代码中的敏感信息检查

重点排查：

- access key
- secret
- token
- password
- private key
- endpoint 中的硬编码凭据

### 3. 日志检查

确认：

- 不打印 token、密码、证件号、完整敏感载荷
- 异常日志不过度暴露内部实现细节
- 请求日志不泄漏敏感字段

### 4. 鉴权一致性检查

确认：

- REST 与 WebSocket 鉴权策略保持一致
- 新增 endpoint 没有绕过现有鉴权
- embedded / edge / cloud 的安全边界未被弱化

### 5. 数据访问安全检查

#### JDBC
- 参数化查询
- 不拼接用户输入到 SQL

#### R2DBC
- 参数化查询
- 不拼接高风险动态 SQL

### 6. 输入校验与异常处理检查

确认：

- 新增 request 参数具备基本校验
- 错误响应不暴露内部实现细节
- 没有把 stack trace 或内部路径直接返回给客户端

### 7. 差异检查

提交前建议检查：

```bash
git diff
git diff --staged
```

重点看：

- 新增配置
- 新增日志
- 新增 SQL
- 新增 endpoint
- 新增脚本

## 输出要求

输出必须包含：

- 检查范围
- 发现的问题
- 风险等级
- 修复建议
- 未发现的问题类型
- 是否适合提交

## 禁止事项

- 不要因为“只是本地调试”就忽略敏感配置
- 不要只检查 Java 代码而忽略脚本和配置文件
- 不要忽略 WebSocket 鉴权路径
- 不要假设 ORM 或框架会自动消除所有注入风险