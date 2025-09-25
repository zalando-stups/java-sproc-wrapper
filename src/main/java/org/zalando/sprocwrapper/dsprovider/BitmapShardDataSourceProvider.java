package org.zalando.sprocwrapper.dsprovider;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.beans.BeanWrapperImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class BitmapShardDataSourceProvider implements DataSourceProvider {

    private static final Logger LOG = LoggerFactory.getLogger(BitmapShardDataSourceProvider.class);

    private final DataSource[] dataSources;

    private final int mask;

    private final List<Integer> distinctShardIds;

    public BitmapShardDataSourceProvider(final Map<String, DataSource> connectionDataSources) {

        int maskLength = 0;
        for (final Entry<String, DataSource> entry : connectionDataSources.entrySet()) {
            if (entry.getKey().length() > maskLength) {
                maskLength = entry.getKey().length();
            }
        }

        mask = (1 << maskLength) - 1;

        dataSources = new DataSource[1 << maskLength];

        for (final Entry<String, DataSource> entry : connectionDataSources.entrySet()) {
            final DataSource ds = entry.getValue();

            for (int i = 0; i < dataSources.length; i++) {
                final String binaryString = Strings.repeat("0", maskLength) + Integer.toBinaryString(i);
                if (binaryString.endsWith(entry.getKey())) {
                    LOG.debug("Configured {} at index {}", entry.getValue(), i);
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

        final Set<DataSource> seenDataSources = Sets.newHashSet();

        for (int i = 0; i < dataSources.length; i++) {
            if (!seenDataSources.contains(dataSources[i])) {
                distinctShardIds.add(i);
                seenDataSources.add(dataSources[i]);
            }
        }
    }

    public BitmapShardDataSourceProvider(final Class<? extends DataSource> dataSourceClass,
            final Map<String, String> commonDataSourceProperties, final Map<String, String> connectionUrls)
        throws InstantiationException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {

        int maskLength = 0;
        for (final Entry<String, String> entry : connectionUrls.entrySet()) {
            if (entry.getKey().length() > maskLength) {
                maskLength = entry.getKey().length();
            }
        }

        mask = (1 << maskLength) - 1;
        dataSources = new DataSource[1 << maskLength];

        for (final Entry<String, String> entry : connectionUrls.entrySet()) {
            final DataSource ds = dataSourceClass.getDeclaredConstructor().newInstance();
            var dsBeanWrapper = new BeanWrapperImpl(ds);
            for (final Entry<String, String> prop : commonDataSourceProperties.entrySet()) {
                dsBeanWrapper.setPropertyValue(prop.getKey(), prop.getValue());
            }

            final String[] parts = entry.getValue().split("\\|");

            dsBeanWrapper.setPropertyValue("jdbcUrl", parts[0]);

            if (parts.length > 1) {

                // a little bit hacky, because "initSQL" is boneCP-specific
                dsBeanWrapper.setPropertyValue("initSQL", parts[1]);
            }

            for (int i = 0; i < dataSources.length; i++) {
                final String binaryString = Strings.repeat("0", maskLength) + Integer.toBinaryString(i);
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

        final Set<DataSource> seenDataSources = Sets.newHashSet();

        for (int i = 0; i < dataSources.length; i++) {
            if (!seenDataSources.contains(dataSources[i])) {
                distinctShardIds.add(i);
                seenDataSources.add(dataSources[i]);
            }
        }

    }

    @Override
    public int getDataSourceId(final int virtualShardId) {
        return virtualShardId & mask;
    }

    @Override
    public DataSource getDataSource(final int virtualShardId) {
        return dataSources[virtualShardId & mask];
    }

    @Override
    public List<Integer> getDistinctShardIds() {
        return distinctShardIds;
    }

}
