package de.zalando.sprocwrapper.dsprovider;

import java.util.List;

import javax.sql.DataSource;

import com.google.common.collect.Lists;

/**
 * @author  jmussler
 */
public class ArrayDataSourceProvider implements DataSourceProvider {
    private DataSource[] dss;

    public ArrayDataSourceProvider(final DataSource[] ds) {
        dss = ds;
    }

    @Override
    public DataSource getDataSource(final int id) {
        return dss[id % dss.length];
    }

    @Override
    public List<Integer> getDistinctShardIds() {
        List<Integer> shardIds = Lists.newArrayListWithExpectedSize(dss.length);
        for (int i = 0; i < dss.length; i++) {
            shardIds.add(i);
        }

        return shardIds;
    }

}
