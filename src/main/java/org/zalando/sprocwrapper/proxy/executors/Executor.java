package org.zalando.sprocwrapper.proxy.executors;

import org.zalando.sprocwrapper.proxy.InvocationContext;

import javax.sql.DataSource;

/**
 * @author  jmussler
 */
public interface Executor {
    Object executeSProc(DataSource ds, String sql, Object[] args, int[] types, InvocationContext invocationContext,
            Class<?> returnType);
}
