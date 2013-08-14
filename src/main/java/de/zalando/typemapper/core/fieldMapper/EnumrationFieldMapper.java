package de.zalando.typemapper.core.fieldMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnumrationFieldMapper implements FieldMapper {

    private static final Logger LOG = LoggerFactory.getLogger(EnumrationFieldMapper.class);

    @SuppressWarnings("rawtypes")
    @Override
    public Object mapField(final String string, final Class clazz) {
        if (string == null) {
            return null;
        }

        if (clazz.getEnumConstants() == null) {
            LOG.warn("{} is not an enum", clazz);
            return null;
        }

        Enum[] enumConstants = (Enum[]) clazz.getEnumConstants();
        try {
            int enumValue = Integer.parseInt(string);
            for (Enum e : enumConstants) {
                if (e.ordinal() == enumValue) {
                    return e;
                }
            }

            LOG.warn("Could not find enum in {} with ordinal {}", clazz, string);
        } catch (NumberFormatException e) {
            for (Enum en : enumConstants) {
                if (en.name().equals(string)) {
                    return en;
                }
            }

            LOG.warn("Could not find enum in {} with name {}", clazz, string);
        }

        return null;
    }

}
