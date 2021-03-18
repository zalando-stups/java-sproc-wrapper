package org.zalando.typemapper.core.fieldMapper;

import java.lang.reflect.InvocationTargetException;

import org.zalando.typemapper.core.ValueTransformer;

public class ValueTransformerFieldMapper implements FieldMapper {

    private final ValueTransformer<?, ?> valueTransformer;

    public ValueTransformerFieldMapper(final Class<? extends ValueTransformer<?, ?>> valueTransformer)
        throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        this.valueTransformer = valueTransformer.getDeclaredConstructor().newInstance();
    }

    public ValueTransformerFieldMapper(final ValueTransformer<?, ?> valueTransformer) {
        this.valueTransformer = valueTransformer;
    }

    @Override
    public Object mapField(final String string, final Class<?> clazz) {
        if (string == null) {
            return null;
        }

        if (valueTransformer != null) {
            return valueTransformer.unmarshalFromDb(string);
        }

        return null;
    }

}
