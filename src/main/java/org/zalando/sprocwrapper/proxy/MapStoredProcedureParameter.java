package org.zalando.sprocwrapper.proxy;

import org.zalando.typemapper.postgres.HStore;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Map;

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
