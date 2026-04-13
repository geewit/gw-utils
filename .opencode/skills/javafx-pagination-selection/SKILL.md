# javafx-pagination-selection

## 目的

为 JavaFX 分页列表、TableView、checkbox 勾选、跨页批量选择提供一致的状态语义，避免出现“翻页后勾选丢失”“批量操作只作用当前页”“当前页全选误伤全量结果集”等常见问题。

## 何时使用

当任务涉及以下内容时必须使用：

- TableView + Pagination
- checkbox 列
- 当前页全选
- 跨页批量操作
- 搜索条件 + 分页列表
- 批量删除 / 批量启用 / 批量导出

## 核心原则

### 1. 必须区分两个状态

- **全局真实状态**：跨页已选 ID 集合 `selectedIds`
- **当前页展示状态**：当前页各行 checkbox 是否勾选

真实选择状态永远以 `selectedIds` 为准，不以当前页可见 checkbox 为准。

### 2. checkbox 只渲染当前页，但选择必须跨页生效

- 当前页 checkbox 只展示当前页数据
- 但用户之前在其他页勾选过的记录，仍应保留在 `selectedIds`
- 翻回原页时，应按 `selectedIds` 正确回填 checkbox

### 3. 当前页全选只作用当前页

- 选中当前页全选：把当前页所有 ID 加入 `selectedIds`
- 取消当前页全选：把当前页所有 ID 从 `selectedIds` 移除
- 不要把“当前页全选”实现成“结果集全选”
- 如业务确实支持“全部结果集全选”，必须单独设计明确入口与语义提示

### 4. 批量操作基于全局已选集合

- 批量删除/导出/启用等操作，必须基于 `selectedIds`
- 不得只基于当前页可见数据
- 操作前必须向用户明确展示累计已选数量

### 5. 刷新后的选择恢复

- 当前页刷新后，应根据 `selectedIds` 恢复当前页 checkbox
- 若某些已选记录已失效或已被删除，应从 `selectedIds` 中移除
- 批量操作成功后，默认清空 `selectedIds`，除非业务明确要求保留

## 推荐状态模型

推荐至少显式维护：

- `searchForm`
- `pageRequest`
- `pageItems`
- `selectedIds`
- `currentPageIds`
- `currentPageSelectedCount`
- `totalSelectedCount`
- `loading`

## 推荐交互

页面建议明确展示：

- 当前页已选数量
- 全部已选数量
- 批量操作作用范围

示例：

- 当前页已选：3
- 全部已选：12

## 输出要求

输出中必须说明：

1. `selectedIds` 如何维护
2. 翻页后 checkbox 如何回填
3. 当前页全选如何实现
4. 批量操作基于什么数据源
5. 数据刷新后如何处理失效选择

## 禁止事项

- 禁止把当前页 checkbox 状态当作唯一真实状态
- 禁止翻页后丢失历史已选记录
- 禁止批量操作只对当前页生效却不提示用户
- 禁止“当前页全选”隐式等于“全部结果集全选”
