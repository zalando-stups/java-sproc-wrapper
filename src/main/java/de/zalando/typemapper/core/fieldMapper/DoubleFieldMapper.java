package de.zalando.typemapper.core.fieldMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DoubleFieldMapper implements FieldMapper {

    private static final Logger LOG = LoggerFactory.getLogger(DoubleFieldMapper.class);

    @Override
    public Object mapField(final String string, final Class clazz) {
        try {
            return string == null ? null : Double.parseDouble(string);
        } catch (NumberFormatException e) {
            LOG.error("Could not convert {} to double.", string, e);
        }

        return null;
    }
}
