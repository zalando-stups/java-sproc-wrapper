package org.zalando.typemapper.core.fieldMapper;

import org.zalando.typemapper.core.result.DbResultNode;
import org.zalando.typemapper.postgres.PgTypeHelper;

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
    public PgTypeHelper.PgTypeDataHolder marshalToDb(final Object value) {
        return null;
    }
}
