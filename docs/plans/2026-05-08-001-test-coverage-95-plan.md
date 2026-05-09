# 全模块测试覆盖率提升至95%实施计划

## 现状分析

当前18个模块的覆盖率情况：

| 模块 | 当前覆盖率 | 目标 | 差距 | 主要难点 |
|------|-----------|------|------|---------|
| core/codec | 92% | 95% | 小 | 补充边界分支 |
| core/date | 100% | 95% | ✅ | 已达目标 |
| core/enums | 98% | 95% | ✅ | 已达目标 |
| core/exceptions | 96% | 95% | ✅ | 已达目标 |
| core/jackson | 93% | 95% | 小 | 补充 Money/Password/Radio 序列化器 |
| core/lang | 100% | 95% | ✅ | 已达目标 |
| core/reflection | 93% | 95% | 小 | 补充异常处理分支 |
| core/tree | 93% | 95% | 小 | 测试4个 Consumer 接口 |
| core/uuid | 84% | 95% | 大 | 15个内部类（codec/factory/util） |
| data/commons | 0% | 95% | 大 | 零测试基础 |
| data/spring | 0% | 95% | 大 | 零测试基础，需Mock Spring Data Page |
| i18n | 0% | 95% | 大 | 零测试基础，LocaleContextHolder处理 |
| web/converter | 0% | 95% | 大 | 零测试基础，5个Spring Converter |
| web/core | 0% | 95% | 大 | 零测试基础，ApplicationContext测试 |
| web/json | 0% | 95% | 大 | 零测试基础，Jackson 3工具 |
| javafx/base | 1% | 95% | 极大 | UI组件+对话框+调度器，~1,159 LOC |
| javafx/controls | 1% | 95% | 极大 | UI皮肤+控件，~1,125 LOC |
| javafx/spring | 0% | 95% | 极大 | Spring+JavaFX集成，~975 LOC |

## 依赖准备

### 1. 添加 Mockito 依赖

settings.gradle 已配置 mockito 版本（5.20.0），但缺少 `mockito-core` library 定义：

```groovy
library('mockito-core', 'org.mockito', 'mockito-core').versionRef('mockito')
```

为以下模块的 build.gradle 添加 `testImplementation(libs.mockito.core)`：
- data/spring, i18n, web/core, web/json, javafx/spring
- core/uuid（用于 mock 内部依赖）

### 2. 添加 TestFX 依赖（javafx模块）

当前 javafx 模块没有 TestFX。要达到95%，必须添加：

```groovy
testImplementation 'org.testfx:testfx-core:4.0.18'
testImplementation 'org.testfx:testfx-junit5:4.0.18'
testImplementation 'org.testfx:openjfx-monocle:jdk-11+26'
```

## 实施阶段

### 第一阶段：零测试基础模块（data/web/i18n）

**预估工作量：中等（~1,000行测试代码）**

1. **data/commons** - ListBatchPageUtils（分页消费工具）
2. **data/spring** - CollectionPageableLoads（7个分页加载方法 + 5个函数接口）
3. **i18n** - CurrencyChineseUtils（人民币大写转换）、I18nSupport（消息解析）
4. **web/converter** - 5个 Spring Data Converter（Date/EnumName/EnumValue读写）
5. **web/core** - SpringContextUtil（ApplicationContext工具）
6. **web/json** - JsonUtils（Jackson 3 JSON序列化工具）

### 第二阶段：core模块补充

**预估工作量：中等（~1,500行测试代码）**

1. **core/jackson** - MoneySerializer、PasswordSerializer、RadioSerializer、EnumNameDeserializer、EnumValueDeserializer
2. **core/tree** - CompressChildConsumer、SignChildConsumer、SignParentConsumer、TransmissionChildConsumer
3. **core/uuid内部类** - 约15个类：
   - UuidCreator（ facade，含 Proxy 并发）
   - UuidFactory（抽象工厂 + Parameters）
   - AbstRandomBasedFactory（FastRandom/SafeRandom）
   - BaseNCodec、BaseN（编码表逻辑）
   - RandomUtil、SettingsUtil、ByteUtil、JavaVersionUtil
   - InvalidUuidException、UuidVersion、UuidCodec

### 第三阶段：javafx模块（最大挑战）

**预估工作量：极大（~3,000+行测试代码）**

**javafx/base** 关键类：
- PaginationSupport.Params（纯逻辑，最易测试）
- VirtualThreadScheduler（无需FX线程）
- UniqueDialogManager（需要 FX 线程或 TestFX）
- FxAsyncActions（Reactor + JavaFX 调度器）
- ConfirmDialogHelper、ImagePreviewDialog（对话框，需 TestFX）
- GwScene、FxScheduler（需 FX 运行时）
- 其他工具类（FxClicks、FxFutures、TooltipUtils等）

**javafx/controls** 关键类：
- PagedCrudTableConfig.Builder（纯逻辑，易测试）
- PageResult、RowAction、MessageProvider（已部分测试）
- Actions（CRUD操作逻辑）
- NumericTextField（正则+格式化逻辑可独立测试）
- PagedCrudTableSkin、PagedCrudTableControl（UI组件，需TestFX）

**javafx/spring** 关键类：
- LazyFxControllerAndView、SimpleFxControllerAndView（纯逻辑）
- FxWeaver.buildFxmlReference、resolveResourcePath（可独立测试）
- InjectionPointLazyFxControllerAndViewResolver（需 Spring 测试）
- FxWeaverAutoConfiguration（需 @SpringBootTest）
- FxWeaver.load/loadController（需 FXML 资源或 mock）

## 风险评估

1. **javafx模块风险高**：大量UI代码需要 JavaFX 运行时，没有 TestFX 的话很多路径无法覆盖
2. **Mockito版本兼容性**：当前配置 mockito-inline 5.2.0 但版本管理用 5.20.0，需要确认是否统一
3. **时间成本**：全部完成预计需要 5,000+ 行测试代码，是现有测试代码的 3-4 倍

## 实施单元详细清单

### 单元1：依赖配置（前置条件）

**目标**：为所有模块添加必要的测试依赖

**文件**：
- `settings.gradle` - 添加 `mockito-core` library 定义
- `build.gradle`（根）- 为子项目统一添加 mockito 条件依赖逻辑，覆盖率阈值统一改为95%
- 各模块 `build.gradle` - 按需添加 mockito 和/或 TestFX

### 单元2：data/commons 测试

**目标**：覆盖 ListBatchPageUtils（38 LOC）

**测试文件**：`data/commons/src/test/java/io/geewit/utils/data/commons/ListBatchPageUtilsTest.java`

**测试场景**：
- 正常分页：list=100条, pageSize=10 -> 调用10次consumer
- pageSize大于list：调用1次consumer
- pageSize=1：每条单独调用
- 空列表：不调用consumer
- null list/consumer/pageSize<=0：抛出IllegalArgumentException

### 单元3：data/spring 测试

**目标**：覆盖 CollectionPageableLoads（368 LOC）

**测试文件**：`data/spring/src/test/java/io/geewit/utils/data/spring/CollectionPageableLoadsTest.java`

**测试场景**：
- pageLoad: 正常分页加载，验证Consumer执行次数和参数
- pageLoad + Count: 带计数验证
- pageLoad + Custom: 自定义分页器
- pageMap: 分页映射转换
- pageForEach: 分页遍历
- 所有函数接口的单元测试
- null参数验证

### 单元4：i18n 测试

**目标**：覆盖 CurrencyChineseUtils + I18nSupport（203 LOC）

**测试文件**：
- `i18n/src/test/java/io/geewit/utils/i18n/CurrencyChineseUtilsTest.java`
- `i18n/src/test/java/io/geewit/utils/i18n/I18nSupportTest.java`

**测试场景**：
- CurrencyChineseUtils: 0元、小数、负数、大整数转换
- I18nSupport: 消息解析、参数插值、Locale切换、fallback处理
- LocaleContextHolder集成测试（使用 ReflectionTestUtils 或 @SpringBootTest）

### 单元5：web/converter 测试

**目标**：覆盖 5 个 Spring Converter（185 LOC）

**测试文件**：`web/converter/src/test/java/io/geewit/utils/web/converter/*ConverterTest.java`

**测试场景**：
- DateWriteConverter: Date -> Long 转换
- DateReadConverter: Long -> Date 转换
- EnumNameReadConverter: String -> Enum（按name）
- EnumValueReadConverter: String -> Enum（按value）
- EnumValueWriteConverter: Enum -> String（value输出）
- null值处理、不支持的类型处理

### 单元6：web/core 测试

**目标**：覆盖 SpringContextUtil（54 LOC）

**测试文件**：`web/core/src/test/java/io/geewit/utils/web/core/SpringContextUtilTest.java`

**测试场景**：
- 使用 @SpringBootTest 或 Mockito.mock(ApplicationContext)
- getBean、getBeansOfType、getEnvironment 等方法
- ApplicationContext未初始化时的行为

### 单元7：web/json 测试

**目标**：覆盖 JsonUtils（217 LOC）

**测试文件**：`web/json/src/test/java/io/geewit/utils/web/json/JsonUtilsTest.java`

**测试场景**：
- toJson: 对象序列化
- parseObject: JSON反序列化
- parseArray: JSON数组反序列化
- 使用 Jackson 3 API（tools.jackson 命名空间）
- null/空字符串处理
- 异常处理（无效JSON）

### 单元8：core/jackson 补充测试

**目标**：从93%提升到95%，补充5个Serializer/Deserializer

**测试文件**：`core/jackson/src/test/java/io/geewit/utils/core/jackson/AdditionalSerializersTest.java`

**测试场景**：
- MoneySerializer: Money对象 -> JSON字符串
- PasswordSerializer: Password对象 -> JSON（脱敏逻辑）
- RadioSerializer: Radio对象 -> JSON（枚举+显示值）
- EnumNameDeserializer: JSON字符串 -> Enum（name匹配）
- EnumValueDeserializer: JSON字符串 -> Enum（value匹配）

### 单元9：core/tree 补充测试

**目标**：从93%提升到95%，测试4个Consumer接口

**测试文件**：`core/tree/src/test/java/io/geewit/utils/core/tree/ConsumersTest.java`

**测试场景**：
- CompressChildConsumer: 子节点压缩逻辑
- SignChildConsumer: 子节点签名
- SignParentConsumer: 父节点签名
- TransmissionChildConsumer: 属性透传

### 单元10：core/uuid 内部类测试

**目标**：从84%提升到95%，覆盖15个内部类

**测试文件**：`core/uuid/src/test/java/io/geewit/utils/core/uuid/*Test.java`（约10个测试类）

**关键测试场景**：
- UuidCreator: 所有create*方法、并发Proxy调用
- UuidFactory: 抽象方法、Parameters构造
- AbstRandomBasedFactory: FastRandom vs SafeRandom
- BaseNCodec: 编码/解码循环、BaseN表格查找
- RandomUtil: 随机数生成
- SettingsUtil: 配置读取
- ByteUtil: 字节操作
- JavaVersionUtil: 版本检测（含当前Java 25环境）
- InvalidUuidException: 异常构造
- UuidVersion: 所有版本枚举值
- UuidCodec: 编解码接口

### 单元11：javafx/base 测试

**目标**：从1%提升到95%，覆盖约1,159 LOC

**测试文件**：约8-10个测试类

**优先测试纯逻辑类**：
- PaginationSupport.Params: Builder模式、属性设置/获取
- VirtualThreadScheduler: 调度逻辑（无需FX线程）
- UniqueDialogManager: 对话框唯一性管理（需FX线程或TestFX）
- FxAsyncActions: Reactor + JavaFX Scheduler集成

**UI类测试策略**：
- ConfirmDialogHelper: 使用 TestFX 模拟按钮点击
- ImagePreviewDialog: 测试构造和属性设置
- GwScene: 测试场景创建和组件查找
- FxScheduler: 测试调度器工厂方法
- FxClicks、FxFutures、TooltipUtils: 工具方法

### 单元12：javafx/controls 测试

**目标**：从1%提升到95%，覆盖约1,125 LOC

**测试文件**：约8-10个测试类

**优先测试纯逻辑类**：
- PagedCrudTableConfig.Builder: Builder模式、默认值
- PageResult: 分页结果构造
- RowAction: 行操作定义
- MessageProvider: 消息提供
- Actions: CRUD操作逻辑

**UI控件测试**：
- NumericTextField: 正则验证、格式化（可独立测试输入处理逻辑）
- PagedCrudTableSkin: 皮肤创建（需TestFX）
- PagedCrudTableControl: 控件行为（需TestFX）

### 单元13：javafx/spring 测试

**目标**：从0%提升到95%，覆盖约975 LOC

**测试文件**：约5-6个测试类

**测试场景**：
- LazyFxControllerAndView: 懒加载逻辑
- SimpleFxControllerAndView: 简单实现
- FxWeaver: FXML引用构建、资源路径解析
- InjectionPointLazyFxControllerAndViewResolver: Spring依赖解析
- FxWeaverAutoConfiguration: 自动配置（需@SpringBootTest）

## 验证策略

1. 每完成一个单元，运行 `gradle :模块名:test jacocoTestReport`
2. 每完成一个阶段，运行 `gradle check` 验证全项目
3. 最终验证：`gradle check` 全部通过，JaCoCo验证所有模块>=95%

## 时间估算

| 阶段 | 单元 | 预估时间 | 测试代码量 |
|------|------|---------|-----------|
| 前置 | 单元1 | 30分钟 | 0 |
| 第一阶段 | 单元2-7 | 2-3小时 | ~1,000行 |
| 第二阶段 | 单元8-10 | 3-4小时 | ~1,500行 |
| 第三阶段 | 单元11-13 | 4-6小时 | ~3,000行 |
| **总计** | | **10-13小时** | **~5,500行** |
