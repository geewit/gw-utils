package io.geewit.web.convert.converter;

import io.geewit.core.utils.enums.EnumUtils;
import io.geewit.core.utils.enums.Value;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalConverter;
import org.springframework.core.convert.converter.Converter;

/**
 * 自动转换类型
 * @param <N>
 * @param <E>
 * @author geewit
 */
public class EnumValueReadConverter<E extends Enum<E> & Value<N>, N extends Number> implements Converter<N, E>, ConditionalConverter {

    private TypeDescriptor targetType;

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (!sourceType.isAssignableTo(TypeDescriptor.valueOf(int.class))
                && !sourceType.isAssignableTo(TypeDescriptor.valueOf(long.class))
                && !sourceType.isAssignableTo(TypeDescriptor.valueOf(Number.class))) {
            return false;
        }
        if (targetType.isAssignableTo(TypeDescriptor.valueOf(Enum.class)) && targetType.isAssignableTo(TypeDescriptor.valueOf(Value.class))) {
            this.targetType = targetType;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public E convert(N source) {
        switch (source) {
            case Integer _ -> {
                try {
                    return EnumUtils.forValue((Class<E>) this.targetType.getObjectType(), source);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            case Long _ -> {
                try {
                    return EnumUtils.forValue((Class<E>) this.targetType.getObjectType(), source);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            default -> {
                return null;
            }
        }

    }
}
