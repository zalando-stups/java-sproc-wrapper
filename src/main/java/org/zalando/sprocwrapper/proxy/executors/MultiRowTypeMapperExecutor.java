package org.zalando.sprocwrapper.proxy.executors;

import org.springframework.jdbc.core.JdbcTemplate;
import org.zalando.sprocwrapper.proxy.InvocationContext;
import org.zalando.typemapper.core.TypeMapperFactory;

import javax.sql.DataSource;

/**
 * @author  jmussler
 */
public class MultiRowTypeMapperExecutor implements Executor {
    @Override
    public Object executeSProc(final DataSource ds, final String sql, final Object[] args, final int[] types,
            final InvocationContext invocationContext, final Class<?> returnType) {
        return (new JdbcTemplate(ds)).query(sql, args, types, TypeMapperFactory.createTypeMapper(returnType));
    }

}
