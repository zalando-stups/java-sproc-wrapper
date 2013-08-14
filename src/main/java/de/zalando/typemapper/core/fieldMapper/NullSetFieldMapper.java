package de.zalando.typemapper.core.fieldMapper;

/**
 * sentinel mapper for NULL fields returned from database for Set<?> mappings to avoid "Could not find mapper for type
 * java.util.Set" exceptions.
 *
 * @author  carsten.wolters
 */
public class NullSetFieldMapper implements FieldMapper {

    @Override
    public Object mapField(final String string, final Class<?> clazz) {
        return null;
    }

}
