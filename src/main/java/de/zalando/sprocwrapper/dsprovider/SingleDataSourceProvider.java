package de.zalando.sprocwrapper.dsprovider;

import java.util.List;

import javax.sql.DataSource;

import com.google.common.collect.Lists;

public class SingleDataSourceProvider implements DataSourceProvider {
    private DataSource dataSource;

    public SingleDataSourceProvider() { }

    public SingleDataSourceProvider(final DataSource ds) {
        dataSource = ds;
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
