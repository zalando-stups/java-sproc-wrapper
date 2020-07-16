package org.zalando.typemapper.core.fieldMapper;

/**
 * sentinel mapper for NULL fields returned from database. Has to be registered as last mapper in
 * {@link FieldMapperRegister}.
 */
public class NullFieldMapper implements FieldMapper {

    @Override
    public Object mapField(final String string, final Class clazz) {
        if (string != null) {
            throw new IllegalStateException("Could not find mapper for type " + clazz);
        } else {
            return null;
        }
    }

}
