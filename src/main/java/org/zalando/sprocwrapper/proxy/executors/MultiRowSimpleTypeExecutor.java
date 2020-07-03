package org.zalando.sprocwrapper.proxy.executors;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;


import org.zalando.sprocwrapper.proxy.InvocationContext;

/**
 * @author  jmussler
 */
public class MultiRowSimpleTypeExecutor implements Executor {

    @Override
    public Object executeSProc(final DataSource ds, final String sql, final Object[] args, final int[] types,
                               final InvocationContext invocationContext, final Class<?> returnType) {
        return (new JdbcTemplate(ds)).queryForList(sql, args, types,
                SingleRowSimpleTypeExecutor.mapReturnType(returnType));
    }
}
