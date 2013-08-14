package de.zalando.typemapper.core.db;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DbType {

    private final String schema;
    private final String name;
    private Map<Integer, DbTypeField> fields = new HashMap<Integer, DbTypeField>();

    public DbType(final String schema, final String name) {
        this.schema = schema;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getSchema() {
        return schema;
    }

    @Override
    public String toString() {
        return "DbType [schema=" + schema + ", name=" + name + ", fields=" + fields + "]";
    }

    public DbTypeField getFieldByPos(final int i) {
        return fields.get(i);
    }

    public Collection<DbTypeField> getFields() {
        return fields.values();
    }

    public void addField(final DbTypeField dbTypeField) {
        fields.put(dbTypeField.getPosition(), dbTypeField);

    }
}
