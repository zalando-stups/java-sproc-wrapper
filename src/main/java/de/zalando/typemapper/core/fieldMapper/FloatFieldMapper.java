package de.zalando.typemapper.core.fieldMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FloatFieldMapper implements FieldMapper {

    private static final Logger LOG = LoggerFactory.getLogger(BooleanFieldMapper.class);

    @Override
    public Object mapField(final String string, final Class clazz) {
        try {
            return string == null ? null : Float.parseFloat(string);
        } catch (NumberFormatException e) {
            LOG.error("Could not convert {} to float.", string, e);
        }

        return null;
    }

}
