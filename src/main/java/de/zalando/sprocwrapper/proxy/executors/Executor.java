package de.zalando.sprocwrapper.proxy.executors;

import javax.sql.DataSource;

/**
 * @author  jmussler
 */
public interface Executor {
    Object executeSProc(DataSource ds, String sql, Object[] args, int[] types, Object[] originalArgs, Class returnType);
}
