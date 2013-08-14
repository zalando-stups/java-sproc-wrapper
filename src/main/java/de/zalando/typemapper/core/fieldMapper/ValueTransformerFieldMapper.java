package de.zalando.typemapper.core.fieldMapper;

import de.zalando.typemapper.core.ValueTransformer;

public class ValueTransformerFieldMapper implements FieldMapper {

    private final ValueTransformer<?, ?> valueTransformer;

    public ValueTransformerFieldMapper(final Class<? extends ValueTransformer<?, ?>> valueTransformer)
        throws InstantiationException, IllegalAccessException {
        this.valueTransformer = valueTransformer.newInstance();
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
