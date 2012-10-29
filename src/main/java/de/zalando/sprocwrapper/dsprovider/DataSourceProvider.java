package de.zalando.sprocwrapper.dsprovider;

import java.util.List;

import javax.sql.DataSource;

/**
 * @author  jmussler
 */
public interface DataSourceProvider {
    DataSource getDataSource(int virtualShardId);

    List<Integer> getDistinctShardIds();
}
