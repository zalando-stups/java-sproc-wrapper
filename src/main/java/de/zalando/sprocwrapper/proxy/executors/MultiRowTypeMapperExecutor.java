package de.zalando.sprocwrapper.proxy.executors;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import de.zalando.typemapper.core.TypeMapperFactory;

/**
 * @author  jmussler
 */
public class MultiRowTypeMapperExecutor implements Executor {
    @Override
    public Object executeSProc(final DataSource ds, final String sql, final Object[] args, final int[] types,
            final Object[] originalArgs, final Class returnType) {
        return (new JdbcTemplate(ds)).query(sql, args, types, TypeMapperFactory.createTypeMapper(returnType));
    }

}
