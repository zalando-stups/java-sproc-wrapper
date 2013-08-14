package de.zalando.typemapper.core.fieldMapper;

public class StringFieldMapper implements FieldMapper {

    @Override
    public Object mapField(final String string, final Class clazz) {
        return string;
    }

}
