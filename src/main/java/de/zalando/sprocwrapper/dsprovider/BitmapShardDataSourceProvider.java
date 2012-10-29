package de.zalando.sprocwrapper.dsprovider;

import java.lang.reflect.InvocationTargetException;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;

import org.apache.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class BitmapShardDataSourceProvider implements DataSourceProvider {

    private static final Logger LOG = Logger.getLogger(BitmapShardDataSourceProvider.class);

    private final DataSource[] dataSources;

    private final int mask;

    private final List<Integer> distinctShardIds;

    public BitmapShardDataSourceProvider(final Map<String, DataSource> connectionDataSources) {

        int maskLength = 0;
        for (Entry<String, DataSource> entry : connectionDataSources.entrySet()) {
            if (entry.getKey().length() > maskLength) {
                maskLength = entry.getKey().length();
            }
        }

        mask = (1 << maskLength) - 1;

        dataSources = new DataSource[1 << maskLength];

        for (Entry<String, DataSource> entry : connectionDataSources.entrySet()) {
            DataSource ds = entry.getValue();

            for (int i = 0; i < dataSources.length; i++) {
                String binaryString = StringUtils.repeat("0", maskLength) + Integer.toBinaryString(i);
                if (binaryString.endsWith(entry.getKey())) {
                    LOG.debug("Configured " + entry.getValue() + " at index " + i);
                    if (dataSources[i] != null) {
                        throw new IllegalArgumentException(
                            "Bitmask misconfigured for shards: two connections configured for index " + i);
                    }

                    dataSources[i] = ds;
                }
            }
        }

        for (int i = 0; i < dataSources.length; i++) {
            if (dataSources[i] == null) {
                throw new IllegalArgumentException("Not enough connection URLs configured for mask length " + maskLength
                        + ": datasource at index " + i + " is missing");
            }
        }

        distinctShardIds = Lists.newArrayList();

        Set<DataSource> seenDataSources = Sets.newHashSet();

        for (int i = 0; i < dataSources.length; i++) {
            if (!seenDataSources.contains(dataSources[i])) {
                distinctShardIds.add(i);
                seenDataSources.add(dataSources[i]);
            }
        }
    }

    public BitmapShardDataSourceProvider(final Class<? extends DataSource> dataSourceClass,
            final Map<String, String> commonDataSourceProperties, final Map<String, String> connectionUrls)
        throws InstantiationException, IllegalAccessException, InvocationTargetException {

        int maskLength = 0;
        for (Entry<String, String> entry : connectionUrls.entrySet()) {
            if (entry.getKey().length() > maskLength) {
                maskLength = entry.getKey().length();
            }
        }

        mask = (1 << maskLength) - 1;
        dataSources = new DataSource[1 << maskLength];

        for (Entry<String, String> entry : connectionUrls.entrySet()) {
            DataSource ds = dataSourceClass.newInstance();
            for (Entry<String, String> prop : commonDataSourceProperties.entrySet()) {
                BeanUtils.setProperty(ds, prop.getKey(), prop.getValue());
            }

            String[] parts = entry.getValue().split("\\|");

            BeanUtils.setProperty(ds, "jdbcUrl", parts[0]);

            if (parts.length > 1) {

                // a little bit hacky, because "initSQL" is boneCP-specific
                BeanUtils.setProperty(ds, "initSQL", parts[1]);
            }

            for (int i = 0; i < dataSources.length; i++) {
                String binaryString = StringUtils.repeat("0", maskLength) + Integer.toBinaryString(i);
                if (binaryString.endsWith(entry.getKey())) {
                    LOG.debug("Configured " + entry.getValue() + " at index " + i);
                    if (dataSources[i] != null) {
                        throw new IllegalArgumentException(
                            "Bitmask misconfigured for shards: two connections configured for index " + i);
                    }

                    dataSources[i] = ds;
                }
            }
        }

        for (int i = 0; i < dataSources.length; i++) {
            if (dataSources[i] == null) {
                throw new IllegalArgumentException("Not enough connection URLs configured for mask length " + maskLength
                        + ": datasource at index " + i + " is missing");
            }
        }

        distinctShardIds = Lists.newArrayList();

        Set<DataSource> seenDataSources = Sets.newHashSet();

        for (int i = 0; i < dataSources.length; i++) {
            if (!seenDataSources.contains(dataSources[i])) {
                distinctShardIds.add(i);
                seenDataSources.add(dataSources[i]);
            }
        }

    }

    /**
     * Letzten 3 Bit bestimmen 8 Shards.
     */
    @Override
    public DataSource getDataSource(final int virtualShardId) {
        return dataSources[virtualShardId & mask];
    }

    @Override
    public List<Integer> getDistinctShardIds() {
        return distinctShardIds;
    }

}
