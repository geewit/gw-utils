package io.geewit.utils.web.converter;

import io.geewit.utils.core.enums.EnumUtils;
import io.geewit.utils.core.enums.Name;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalConverter;
import org.springframework.core.convert.converter.Converter;


/**
 * 自动转换类型
 * @param <T>
 * @author geewit
 */
public class EnumNameReadConverter<T extends Enum<T> & Name> implements Converter<String, T>, ConditionalConverter {

    private TypeDescriptor targetType;

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (!sourceType.isAssignableTo(TypeDescriptor.valueOf(String.class))) {
            return false;
        }
        if (targetType.isAssignableTo(TypeDescriptor.valueOf(Enum.class)) && targetType.isAssignableTo(TypeDescriptor.valueOf(Name.class))) {
            this.targetType = targetType;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public T convert(String source) {
        return EnumUtils.forToken((Class<T>) this.targetType.getObjectType(), source);
    }
}
