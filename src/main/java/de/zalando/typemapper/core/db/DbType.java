package de.zalando.typemapper.core.db;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DbType {

    private final String schema;
    private final String name;
    private final int id;
    private Map<Integer, DbTypeField> fields = new HashMap<Integer, DbTypeField>();

    public DbType(final String schema, final String name, final int id) {
        this.schema = schema;
        this.name = name;
        this.id = id;
    }

    public String getSchema() {
        return schema;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
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

    @Override
    public String toString() {
        return "DbType [schema=" + schema + ", name=" + name + ", id=" + id + ", fields=" + fields + "]";
    }
}
