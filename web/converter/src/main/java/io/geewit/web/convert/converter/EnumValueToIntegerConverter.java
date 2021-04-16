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
public class EnumValueToIntegerConverter<S extends Enum<S> & Value> implements Converter<S, Integer>, ConditionalConverter {

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return sourceType.isAssignableTo(TypeDescriptor.valueOf(Value.class)) && sourceType.isAssignableTo(TypeDescriptor.valueOf(Enum.class));
    }

    @Override
    public Integer convert(S source) {
        return source.value();
    }
}
