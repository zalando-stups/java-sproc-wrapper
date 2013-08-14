package de.zalando.typemapper.core.fieldMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LongFieldMapper implements FieldMapper {

    private static final Logger LOG = LoggerFactory.getLogger(LongFieldMapper.class);

    @Override
    public Object mapField(final String string, final Class clazz) {
        if (string == null) {
            return null;
        }

        try {
            return Long.parseLong(string);
        } catch (NumberFormatException e) {
            LOG.error("Could not convert {} to long.", string, e);
        }

        return null;
    }

}
