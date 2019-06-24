package org.zalando.sprocwrapper.proxy.executors;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.zalando.sprocwrapper.proxy.InvocationContext;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author  hjacobs
 */
public class SingleRowCustomMapperExecutor implements Executor {

    private final RowMapper<?> resultMapper;

    public SingleRowCustomMapperExecutor(final RowMapper<?> resultMapper) {
        this.resultMapper = resultMapper;
    }

    @Override
    public Object executeSProc(final DataSource ds, final String sql, final Object[] args, final int[] types,
            final InvocationContext invocationContext, final Class<?> returnType) {
        final List<?> list = (new JdbcTemplate(ds)).query(sql, args, types, resultMapper);
        if (!list.isEmpty()) {
            return list.iterator().next();
        }

        return null;
    }

}
