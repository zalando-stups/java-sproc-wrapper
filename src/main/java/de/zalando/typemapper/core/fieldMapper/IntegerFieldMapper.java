package de.zalando.typemapper.core.fieldMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntegerFieldMapper implements FieldMapper {

    private static final Logger LOG = LoggerFactory.getLogger(IntegerFieldMapper.class);

    @Override
    public Object mapField(final String string, final Class clazz) {
        if (string == null) {
            return null;
        }

        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            LOG.error("Could not convert {} to int.", string, e);
        }

        return null;
    }

}
