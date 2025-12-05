package io.geewit.utils.core.jackson.databind.serializer;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * 密码加密（序列化时用 * 掩码）
 * Password masking serializer
 *
 * @author geewit
 * @since 2016/12/20
 */
@SuppressWarnings({"unused"})
public class PasswordSerializer extends ValueSerializer<String> {
    private PasswordSerializer() {
    }

    /**
     * 密码加密（序列化时用 * 掩码）
     * Password masking serializer
     */
    public static final PasswordSerializer instance = new PasswordSerializer();


    /**
     * 序列化字符串值，将原始字符串替换为相同长度的星号字符串进行掩码处理
     *
     * @param value 待序列化的字符串值，可能为null
     * @param generator JSON生成器，用于输出序列化结果
     * @param context 序列化上下文
     * @throws JacksonException 当序列化过程中发生错误时抛出
     */
    @Override
    public void serialize(String value,
                          JsonGenerator generator,
                          SerializationContext context) throws JacksonException {

        if (value != null) {
            // 将非空字符串替换为相同长度的星号字符串
            String masked = repeatStar(value.length());
            generator.writeString(masked);
        } else {
            // 保持原行为：null -> "" （而不是 JSON null）
            generator.writeString("");
        }
    }


    /**
     * 重复生成星号字符
     * @param repeat 重复次数
     * @return 包含指定数量星号的字符串，如果重复次数小于等于0则返回空字符串
     */
    private static String repeatStar(int repeat) {
        if (repeat <= 0) {
            return "";
        }
        // 创建字符数组并填充星号
        char[] buf = new char[repeat];
        for (int i = repeat - 1; i >= 0; --i) {
            buf[i] = '*';
        }
        return new String(buf);
    }
}
