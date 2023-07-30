package io.geewit.web.convert.converter;

import io.geewit.core.utils.enums.EnumUtils;
import io.geewit.core.utils.enums.Value;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalConverter;
import org.springframework.core.convert.converter.Converter;

import java.io.Serializable;


/**
 * 自动转换类型
 * @param <S>
 * @param <E>
 * @author geewit
 */
public class EnumValueConverter<S extends Serializable, E extends Enum<E> & Value<N>, N extends Number> implements Converter<S, E>, ConditionalConverter {

    private TypeDescriptor targetType;

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        if(!sourceType.isAssignableTo(TypeDescriptor.valueOf(String.class))
                && !sourceType.isAssignableTo(TypeDescriptor.valueOf(int.class))
                && !sourceType.isAssignableTo(TypeDescriptor.valueOf(long.class))
                && !sourceType.isAssignableTo(TypeDescriptor.valueOf(Number.class))) {
            return false;
        }
        if(targetType.isAssignableTo(TypeDescriptor.valueOf(Enum.class)) && targetType.isAssignableTo(TypeDescriptor.valueOf(Value.class))) {
            this.targetType = targetType;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public E convert(S source) {
        if(source instanceof Integer) {
            try {
                return EnumUtils.forValue((Class<E>) this.targetType.getObjectType(), (N)source);
            } catch (NumberFormatException e) {
                return null;
            }
        } else if(source instanceof Long) {
            try {
                return EnumUtils.forValue((Class<E>) this.targetType.getObjectType(), (N)source);
            } catch (NumberFormatException e) {
                return null;
            }
        } else if(source instanceof String) {
            try {
                long value = Long.parseLong((String)source);
                return EnumUtils.forValue((Class<E>) this.targetType.getObjectType(), (N)Long.valueOf(value));
            } catch (NumberFormatException e) {
                return null;
            }
        } else {
            return null;
        }

    }
}
