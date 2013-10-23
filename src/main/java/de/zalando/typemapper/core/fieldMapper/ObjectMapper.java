package de.zalando.typemapper.core.fieldMapper;

import com.google.common.base.Preconditions;

import de.zalando.typemapper.core.result.DbResultNode;
import de.zalando.typemapper.postgres.PgTypeHelper.PgTypeDataHolder;

/**
 * @author  danieldelhoyo
 */
public abstract class ObjectMapper<Bound> {

    private final Class<Bound> type;

    public ObjectMapper(final Class<Bound> type) {
        this.type = Preconditions.checkNotNull(type, "type");
    }

    public Class<Bound> getType() {
        return type;
    }

    public abstract Bound unmarshalFromDbNode(DbResultNode dbResultNode);

    public abstract PgTypeDataHolder marshalToDb(Bound value);
}
