package org.zalando.sprocwrapper.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.zalando.sprocwrapper.AbstractSProcService;
import org.zalando.sprocwrapper.dsprovider.ArrayDataSourceProvider;

import java.util.List;

@Repository
public class ShardingSprocServiceImpl extends AbstractSProcService<ShardingSprocService, ArrayDataSourceProvider>
    implements ShardingSprocService {

    @Autowired
    public ShardingSprocServiceImpl(@Qualifier("testShardDataSourceProvider") final ArrayDataSourceProvider p) {
        super(p, ShardingSprocService.class);
    }

    @Override
    public int getShardIndex(final int shard) {
        return sproc.getShardIndex(shard);
    }

    @Override
    public List<String> collectDataFromAllShards(final String someParameter) {
        return sproc.collectDataFromAllShards(someParameter);
    }

    @Override
    public List<String> collectDataFromAllShardsSearchShardsOn(final String someParameter) {
        return sproc.collectDataFromAllShardsSearchShardsOn(someParameter);
    }

    @Override
    public List<String> collectDataFromAllShardsParallel(final String someParameter) {
        return sproc.collectDataFromAllShardsParallel(someParameter);
    }

    @Override
    public List<String> collectDataFromAllShardsParallelSearchShardsOn(final String someParameter) {
        return sproc.collectDataFromAllShardsParallelSearchShardsOn(someParameter);
    }
}
