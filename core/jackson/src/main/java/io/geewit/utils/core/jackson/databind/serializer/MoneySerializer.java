package io.geewit.utils.core.jackson.databind.serializer;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 金额序列化：BigDecimal -> String，保留两位小数
 * Money serializer: BigDecimal -> String with 2 decimals
 *
 * @author geewit
 * @since 2016/12/20
 */
@SuppressWarnings({"unused"})
public class MoneySerializer extends ValueSerializer<BigDecimal> {
    private MoneySerializer() {
    }

    /**
     * 默认实例
     */
    public static final MoneySerializer instance = new MoneySerializer();


    /**
     * 序列化BigDecimal值为JSON格式
     *
     * @param value 需要序列化的BigDecimal值，可能为null
     * @param generator JSON生成器，用于写出序列化结果
     * @param context 序列化上下文
     */
    @Override
    public void serialize(BigDecimal value,
                          JsonGenerator generator,
                          SerializationContext context) {

        if (value == null) {
            // 保持和 Jackson 默认行为一致：写出 null
            generator.writeNull();
            return;
        }

        // 使用 RoundingMode，避免 JDK 9+ 废弃 API
        BigDecimal scaled = value.setScale(2, RoundingMode.HALF_UP);
        generator.writeString(scaled.toString());
    }
}
