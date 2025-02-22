package io.geewit.web.convert.converter;

import io.geewit.core.utils.enums.Name;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalConverter;
import org.springframework.core.convert.converter.Converter;


/**
 * 自动转换类型
 * @param <T>
 * @author geewit
 */
public class EnumNameWriteConverter<T extends Enum<T> & Name> implements Converter<T, String>, ConditionalConverter {

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (!targetType.isAssignableTo(TypeDescriptor.valueOf(String.class))) {
            return false;
        }
        return sourceType.isAssignableTo(TypeDescriptor.valueOf(Enum.class)) && sourceType.isAssignableTo(TypeDescriptor.valueOf(Name.class));
    }

    @Override
    public String convert(T source) {
        return source.getName();
    }
}
