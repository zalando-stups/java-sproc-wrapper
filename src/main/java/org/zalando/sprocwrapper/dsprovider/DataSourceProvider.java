package org.zalando.sprocwrapper.dsprovider;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author  jmussler
 */
public interface DataSourceProvider {
    int getDataSourceId(int virtualShardId);

    DataSource getDataSource(int virtualShardId);

    List<Integer> getDistinctShardIds();
}
