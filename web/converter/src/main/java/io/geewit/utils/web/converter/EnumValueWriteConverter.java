package io.geewit.utils.web.converter;

import io.geewit.utils.core.enums.Value;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalConverter;
import org.springframework.core.convert.converter.Converter;

/**
 * 自动转换类型
 * @param <N>
 * @param <E>
 * @author geewit
 */
public class EnumValueWriteConverter<E extends Enum<E> & Value<N>, N extends Number> implements Converter<E, N>, ConditionalConverter {

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (!targetType.isAssignableTo(TypeDescriptor.valueOf(int.class))
                && !targetType.isAssignableTo(TypeDescriptor.valueOf(long.class))
                && !targetType.isAssignableTo(TypeDescriptor.valueOf(Number.class))) {
            return false;
        }
        return sourceType.isAssignableTo(TypeDescriptor.valueOf(Enum.class)) && sourceType.isAssignableTo(TypeDescriptor.valueOf(Value.class));
    }

    @Override
    public N convert(E source) {
        return source.value();
    }
}
