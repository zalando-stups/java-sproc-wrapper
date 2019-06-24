package org.zalando.sprocwrapper.dsprovider;

import com.google.common.collect.Lists;

import javax.sql.DataSource;
import java.util.List;

public class SingleDataSourceProvider implements DataSourceProvider {
    private DataSource dataSource;

    public SingleDataSourceProvider() { }

    public SingleDataSourceProvider(final DataSource ds) {
        dataSource = ds;
    }

    @Override
    public int getDataSourceId(final int virtualShardId) {
        return 1;
    }

    @Override
    public DataSource getDataSource(final int id) {
        return dataSource;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Integer> getDistinctShardIds() {
        return Lists.newArrayList(1);
    }

}
