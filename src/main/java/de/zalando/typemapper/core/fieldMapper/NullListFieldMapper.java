package de.zalando.typemapper.core.fieldMapper;

/**
 * sentinel mapper for NULL fields returned from database for List<?> mappings to avoid "Could not find mapper for type
 * java.util.List" exceptions.
 *
 * @author  hjacobs
 */
public class NullListFieldMapper implements FieldMapper {

    @Override
    public Object mapField(final String string, final Class clazz) {
        return null;
    }

}
