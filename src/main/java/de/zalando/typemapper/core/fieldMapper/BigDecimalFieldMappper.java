package de.zalando.typemapper.core.fieldMapper;

import java.math.BigDecimal;

public class BigDecimalFieldMappper implements FieldMapper {

    @Override
    public Object mapField(final String string, final Class clazz) {
        if (string == null) {
            return null;
        }

        return new BigDecimal(string);
    }

}
