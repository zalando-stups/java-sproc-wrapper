package de.zalando.typemapper.core.fieldMapper;

import de.zalando.typemapper.core.result.DbResultNode;
import de.zalando.typemapper.postgres.PgTypeHelper.PgTypeDataHolder;

/**
 * @author  danieldelhoyo
 */
public class DefaultObjectMapper extends ObjectMapper<Object> {

    public DefaultObjectMapper() {
        super(Object.class);
    }

    @Override
    public Object unmarshalFromDbNode(final DbResultNode object) {
        return null; // no mapper
    }

    @Override
    public PgTypeDataHolder marshalToDb(final Object value) {
        return null;
    }
}
