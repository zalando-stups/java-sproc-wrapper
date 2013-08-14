package de.zalando.typemapper.core.fieldMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShortFieldMapper implements FieldMapper {

    private static final Logger LOG = LoggerFactory.getLogger(ShortFieldMapper.class);

    @Override
    public Object mapField(final String string, final Class clazz) {
        if (string == null) {
            return null;
        }

        try {
            return Short.parseShort(string);
        } catch (NumberFormatException e) {
            LOG.error("Could not convert {} to short.", string, e);
        }

        return null;
    }

}
