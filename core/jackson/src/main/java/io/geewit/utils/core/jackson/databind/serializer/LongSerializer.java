package io.geewit.utils.core.jackson.databind.serializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;

/**
 * Long 序列化为 String（超过一定长度时）
 * Serialize Long as String when length exceeds threshold
 *
 * @author geewit
 */
@SuppressWarnings({"unused"})
public class LongSerializer extends StdSerializer<Long> {

    private static final Logger logger = LoggerFactory.getLogger(LongSerializer.class);

    /**
     * Long 默认序列化器
     */
    public static final LongSerializer instance = new LongSerializer();

    /**
     * 超过这个长度的数字就按字符串输出
     * threshold length: if value.toString().length() > maxLength, write as String
     */
    private final int maxLength;

    /**
     * Long 默认序列化器
     */
    public LongSerializer() {
        super(Long.class);
        // 约等于 9：Integer.MAX_VALUE ≈ 2_147_483_647（10 位），这里你原来就是这么算的
        this.maxLength = (int) Math.log10(Integer.MAX_VALUE);
    }


    /**
     * 序列化Long类型值到JSON输出
     *
     * @param value 需要序列化的Long值，可能为null
     * @param generator JSON生成器，用于写入序列化结果
     * @param context 序列化上下文，提供序列化过程中的相关信息
     */
    @Override
    public void serialize(Long value,
                          JsonGenerator generator,
                          SerializationContext context) {

        if (value == null) {
            // 和 Jackson 默认行为保持一致：写出 null
            generator.writeNull();
            return;
        }

        String text = value.toString();
        try {
            // 根据数值长度决定输出格式，避免JavaScript精度问题
            if (text.length() > this.maxLength) {
                // 超过阈值，用字符串，避免前端/JS 精度问题
                generator.writeString(text);
            } else {
                // 小数字按正常 number 输出
                generator.writeNumber(value);
            }
        } catch (Exception e) {
            // Jackson 3 抛的是 JacksonException（RuntimeException），这里按你原来的风格打个日志即可
            logger.warn("Failed to serialize Long value '{}': {}", value, e.getMessage(), e);
        }
    }
}
