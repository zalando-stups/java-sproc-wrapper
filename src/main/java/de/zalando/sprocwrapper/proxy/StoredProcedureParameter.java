package de.zalando.sprocwrapper.proxy;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import java.sql.Connection;
import java.sql.Types;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.zalando.sprocwrapper.globalvaluetransformer.GlobalValueTransformerLoader;
import de.zalando.sprocwrapper.util.NameUtils;

import de.zalando.typemapper.core.ValueTransformer;

/**
 * @author  jmussler
 */
class StoredProcedureParameter {

    protected static final Map<Class<?>, Integer> SQL_MAPPING = new HashMap<Class<?>, Integer>();

    static {
        SQL_MAPPING.put(int.class, java.sql.Types.INTEGER);
        SQL_MAPPING.put(Integer.class, java.sql.Types.INTEGER);
        SQL_MAPPING.put(long.class, java.sql.Types.BIGINT);
        SQL_MAPPING.put(Long.class, java.sql.Types.BIGINT);
        SQL_MAPPING.put(float.class, java.sql.Types.FLOAT);
        SQL_MAPPING.put(Float.class, java.sql.Types.FLOAT);
        SQL_MAPPING.put(double.class, java.sql.Types.DOUBLE);
        SQL_MAPPING.put(Double.class, java.sql.Types.DOUBLE);
        SQL_MAPPING.put(String.class, java.sql.Types.VARCHAR);
        SQL_MAPPING.put(java.sql.Date.class, java.sql.Types.TIMESTAMP);
        SQL_MAPPING.put(Date.class, java.sql.Types.TIMESTAMP);
        SQL_MAPPING.put(List.class, java.sql.Types.ARRAY);
        SQL_MAPPING.put(short.class, java.sql.Types.SMALLINT);
        SQL_MAPPING.put(Short.class, java.sql.Types.SMALLINT);
        SQL_MAPPING.put(boolean.class, java.sql.Types.BOOLEAN);
        SQL_MAPPING.put(Boolean.class, java.sql.Types.BOOLEAN);
        SQL_MAPPING.put(char.class, java.sql.Types.CHAR);
        SQL_MAPPING.put(Character.class, java.sql.Types.CHAR);
    }

    protected final String typeName;
    protected final int type;
    protected final int javaPos;
    protected final Class<?> clazz;
    protected final boolean sensitive;

    public static StoredProcedureParameter createParameter(final Class<?> clazz, final Type genericType, final Method m,
            final String typeName, final int sqlType, final int javaPosition, final boolean sensitive)
        throws InstantiationException, IllegalAccessException {

        // first check if this is a globally mapped class
        ValueTransformer<?, ?> valueTransformerForClass = null;
        if (genericType != null) {
            valueTransformerForClass = GlobalValueTransformerLoader.getValueTransformerForClass((Class<?>) genericType);
        }

        if (valueTransformerForClass != null) {

            // inject the additional logic to transform types and values
            return new GlobalValueTransformedParameter(valueTransformerForClass, clazz, genericType, m, typeName,
                    sqlType, javaPosition, sensitive);
        } else {
            Integer typeId = sqlType;
            if (typeId == null || typeId == -1) {
                typeId = SQL_MAPPING.get(clazz);
            }

            // explicitly mapping Map to a hstore, since PgTypeHelper does not fall back to it
            if (typeId == null && Map.class.isAssignableFrom(clazz)) {
                return new MapStoredProcedureParameter(clazz, m, typeName, sqlType, javaPosition, sensitive);
            }

            if (typeId == null) {
                typeId = java.sql.Types.OTHER;
            }

            if (typeId == Types.ARRAY) {
                return new ArrayStoredProcedureParameter(clazz, m, typeName, sqlType, javaPosition, sensitive);
            } else if (typeId == Types.OTHER) {
                return new OtherStoredProcedureParameter(clazz, m, typeName, sqlType, javaPosition, sensitive);
            }

            return new StoredProcedureParameter(clazz, m, typeName, sqlType, javaPosition, sensitive);
        }
    }

    public StoredProcedureParameter(final Class<?> clazz, final Method m, final String typeName, final int sqlType,
            final int javaPosition, final boolean sensitive) {

        this.clazz = clazz;

        Integer typeId = sqlType;
        if (typeId == null || typeId == -1) {
            typeId = SQL_MAPPING.get(clazz);
        }

        if (typeId == null) {
            typeId = java.sql.Types.OTHER;
        }

        type = typeId;

        if (typeName == null || typeName.isEmpty()) {
            this.typeName = NameUtils.camelCaseToUnderscore(clazz.getSimpleName());
        } else {
            this.typeName = typeName;
        }

        javaPos = javaPosition;
        this.sensitive = sensitive;

    }

    public Object mapParam(final Object value, final Connection connection) {
        return value;
    }

    public int getJavaPos() {
        return javaPos;
    }

    public boolean isSensitive() {
        return sensitive;
    }

    public int getType() {
        return type;
    }

    public String getTypeName() {
        return typeName;
    }

}
