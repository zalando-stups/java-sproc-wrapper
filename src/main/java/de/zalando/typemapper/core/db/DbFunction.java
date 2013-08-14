package de.zalando.typemapper.core.db;

import java.util.HashMap;
import java.util.Map;

public class DbFunction {

    private final String schema;
    private final String name;
    private final Map<Integer, DbTypeField> outParams = new HashMap<Integer, DbTypeField>();

    public DbFunction(final String functionSchema, final String functionName) {
        super();
        this.schema = functionSchema;
        this.name = functionName;
    }

    void addOutParam(final DbTypeField field) {
        outParams.put(field.getPosition(), field);
    }

    public String getName() {
        return name;
    }

    public String getSchema() {
        return schema;
    }

    public DbTypeField getFieldByPos(final int i) {
        return outParams.get(i);
    }

    @Override
    public String toString() {
        return "DbFunction [schema=" + schema + ", name=" + name + ", outParams=" + outParams + "]";
    }

}
