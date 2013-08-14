package de.zalando.sprocwrapper.proxy;

import java.lang.reflect.Method;

import java.sql.Connection;

import java.util.Map;

import de.zalando.typemapper.postgres.HStore;

public class MapStoredProcedureParameter extends StoredProcedureParameter {

    public MapStoredProcedureParameter(final Class<?> clazz, final Method m, final String typeName, final int sqlType,
            final int javaPosition, final boolean sensitive) {
        super(clazz, m, typeName, sqlType, javaPosition, sensitive);
    }

    @Override
    public Object mapParam(final Object value, final Connection connection) {
        if (value == null) {
            return null;
        }

        return new HStore((Map<?, ?>) value);
    }

}
