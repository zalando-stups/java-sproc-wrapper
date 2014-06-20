package de.zalando.sprocwrapper.proxy.executors;

import javax.sql.DataSource;

import de.zalando.sprocwrapper.proxy.InvocationContext;

/**
 * @author  jmussler
 */
public interface Executor {
    Object executeSProc(DataSource ds, String sql, Object[] args, int[] types, InvocationContext invocationContext,
            Class<?> returnType);
}
