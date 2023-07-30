package io.geewit.web.convert.converter;

import io.geewit.core.utils.enums.Value;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalConverter;
import org.springframework.core.convert.converter.Converter;


/**
 * 自动转换类型
 * @param <S>
 * @author geewit
 */
public class EnumValueToLongConverter<S extends Enum<S> & Value<Long>> implements Converter<S, Long>, ConditionalConverter {

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return sourceType.isAssignableTo(TypeDescriptor.valueOf(Value.class)) && sourceType.isAssignableTo(TypeDescriptor.valueOf(Enum.class));
    }

    @Override
    public Long convert(S source) {
        return source.value();
    }
}
