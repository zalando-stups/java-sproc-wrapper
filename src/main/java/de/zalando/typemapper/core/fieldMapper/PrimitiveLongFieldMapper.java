package de.zalando.typemapper.core.fieldMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrimitiveLongFieldMapper implements FieldMapper {

    private static final Logger LOG = LoggerFactory.getLogger(PrimitiveLongFieldMapper.class);

    @Override
    public Object mapField(final String string, final Class clazz) {
        if (string == null) {
            return new Long(0);
        }

        try {
            return Long.parseLong(string);
        } catch (NumberFormatException e) {
            LOG.error("Could not convert {} to long.", string, e);
        }

        return new Long(0);
    }

}
