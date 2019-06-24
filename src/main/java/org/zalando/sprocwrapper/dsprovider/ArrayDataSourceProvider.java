package org.zalando.sprocwrapper.dsprovider;

import com.google.common.collect.Lists;

import javax.sql.DataSource;
import java.util.List;

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
        return virtualShardId % dss.length;
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
