package de.zalando.sprocwrapper.dsprovider;

import java.util.List;

import javax.sql.DataSource;

import com.google.common.collect.Lists;

/**
 * @author  jmussler
 */
public class ArrayDataSourceProvider implements DataSourceProvider {
    private final DataSource[] dss;

    public ArrayDataSourceProvider(final DataSource[] ds) {
        dss = ds;
    }

    @Override
    public int getDataSourceId(final int virtualShardId) {
        return virtualShardId % dss.length;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public DataSource getDataSource(final int virtualShardId) {
        return dss[virtualShardId % dss.length];
    }

    @Override
    public List<Integer> getDistinctShardIds() {
        final List<Integer> shardIds = Lists.newArrayListWithExpectedSize(dss.length);

        for (int i = 0; i < dss.length; i++) {
            shardIds.add(i);
        }

        return shardIds;
    }

}
