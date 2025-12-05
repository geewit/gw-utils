package io.geewit.utils.core.jackson.databind.serializer;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 比例序列化（保留 4 位小数）
 * @author hf
 * @since 2016/12/20
 */
@SuppressWarnings({"unused"})
public class RadioSerializer extends ValueSerializer<BigDecimal> {
    private RadioSerializer() {
    }
    /**
     * 单例
     */
    public static final RadioSerializer instance = new RadioSerializer();


    /**
     * 序列化BigDecimal值为JSON字符串
     *
     * @param value 需要序列化的BigDecimal值，可以为null
     * @param generator JSON生成器，用于写入序列化结果
     * @param context 序列化上下文
     * @throws JacksonException 当序列化过程中发生错误时抛出
     */
    @Override
    public void serialize(BigDecimal value,
                          JsonGenerator generator,
                          SerializationContext context) throws JacksonException {

        if (value == null) {
            generator.writeNull();
            return;
        }

        // 保留4位小数，四舍五入
        BigDecimal scaled = value.setScale(4, RoundingMode.HALF_UP);
        generator.writeString(scaled.toString());
    }
}
