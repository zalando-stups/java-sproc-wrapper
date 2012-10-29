package de.zalando.sprocwrapper.proxy.executors;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.postgresql.util.PGobject;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author  jmussler
 */
public class SingleRowSimpleTypeExecutor implements Executor {

    public static final Map<Class, Class> SIMPLE_TYPES = new HashMap<Class, Class>();

    static {

        // sproc with VOID result has type PGobject in Java
        SIMPLE_TYPES.put(Void.class, PGobject.class);
        SIMPLE_TYPES.put(void.class, PGobject.class);
        SIMPLE_TYPES.put(Integer.class, Integer.class);
        SIMPLE_TYPES.put(Short.class, Short.class);
        SIMPLE_TYPES.put(String.class, String.class);
        SIMPLE_TYPES.put(Float.class, Float.class);
        SIMPLE_TYPES.put(Double.class, Double.class);
        SIMPLE_TYPES.put(Long.class, Long.class);
        SIMPLE_TYPES.put(Boolean.class, Boolean.class);
        SIMPLE_TYPES.put(int.class, Integer.class);
        SIMPLE_TYPES.put(long.class, Long.class);
        SIMPLE_TYPES.put(short.class, Short.class);
        SIMPLE_TYPES.put(float.class, Float.class);
        SIMPLE_TYPES.put(double.class, Double.class);
        SIMPLE_TYPES.put(boolean.class, Boolean.class);
        SIMPLE_TYPES.put(Date.class, Date.class);

    }

    public static Class mapReturnType(final Class returnType) {
        final Class clazz = SIMPLE_TYPES.get(returnType);
        if (clazz != null) {
            return clazz;
        }

        return returnType;
    }

    @Override
    public Object executeSProc(final DataSource ds, final String sql, final Object[] args, final int[] types,
            final Object[] originalArgs, final Class returnType) {
        return (new JdbcTemplate(ds)).queryForObject(sql, args, types, mapReturnType(returnType));
    }
}
