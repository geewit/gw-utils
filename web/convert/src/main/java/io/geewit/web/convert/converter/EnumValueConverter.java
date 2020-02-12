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
 * @param <T>
 * @author geewit
 */
public class EnumValueConverter<S extends Serializable, T extends Enum<T> & Value> implements Converter<S, T>, ConditionalConverter {

    private TypeDescriptor targetType;

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        if(!sourceType.isAssignableTo(TypeDescriptor.valueOf(String.class)) && !sourceType.isAssignableTo(TypeDescriptor.valueOf(int.class)) && !sourceType.isAssignableTo(TypeDescriptor.valueOf(Integer.class))) {
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
    public T convert(S source) {
        if(source instanceof Integer) {
            try {
                return EnumUtils.forToken((Class<T>) this.targetType.getObjectType(), (Integer)source);
            } catch (NumberFormatException e) {
                return null;
            }
        } else if(source instanceof String) {
            try {
                int value = Integer.parseInt((String)source);
                return EnumUtils.forToken((Class<T>) this.targetType.getObjectType(), value);
            } catch (NumberFormatException e) {
                return null;
            }
        } else {
            return null;
        }

    }
}
