package de.zalando.sprocwrapper.proxy.executors;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

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
            final Object[] originalArgs, final Class returnType) {
        final List list = (new JdbcTemplate(ds)).query(sql, args, types, resultMapper);
        if (!list.isEmpty()) {
            return list.iterator().next();
        }

        return null;
    }

}
