package org.zalando.typemapper.core.fieldMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Created by akushsky on 27.08.2015.
 */
public class UUIDFieldMapper implements FieldMapper {

    private static final Logger LOG = LoggerFactory.getLogger(UUIDFieldMapper.class);

    @Override
    public Object mapField(String string, Class<?> clazz) {
        if (string == null) {
            return null;
        }

        try {
            return UUID.fromString(string);
        } catch (IllegalArgumentException e) {
            LOG.error("Could not convert {} to UUID.", string);
            return null;
        }
    }
}
