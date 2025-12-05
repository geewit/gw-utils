package io.geewit.utils.core.jackson.databind.serializer;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * BigDecimal 类型序列化 / BigDecimal serializer
 * @author geewit
 * @since  2016/12/20
 */
@SuppressWarnings({"unused"})
public class BigDecimalSerializer extends ValueSerializer<BigDecimal> {
    private BigDecimalSerializer() {
    }

    /**
     * 默认实例
     */
    public static final BigDecimalSerializer instance = new BigDecimalSerializer();


    /**
     * 序列化BigDecimal值为JSON格式
     *
     * @param value 需要序列化的BigDecimal值，可能为null
     * @param generator JSON生成器，用于输出序列化结果
     * @param context 序列化上下文，提供序列化相关的配置和工具方法
     * @throws JacksonException 当序列化过程中发生错误时抛出
     */
    @Override
    public void serialize(BigDecimal value,
                          JsonGenerator generator,
                          SerializationContext context) throws JacksonException {

        // 一般情况下 Jackson 不会用这个 serializer 处理 null，
        // 但加一下保护也没坏处
        if (value == null) {
            // 方案1：直接写 null
            generator.writeNull();

            // 如果你更想用 Jackson 的默认 null 策略，可以用：
            // context.defaultSerializeNull(generator);
            return;
        }

        // 将BigDecimal值四舍五入到2位小数并转换为字符串进行序列化
        BigDecimal scaled = value.setScale(2, RoundingMode.HALF_UP);
        generator.writeString(scaled.toString());
    }
}
