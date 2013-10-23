package de.zalando.sprocwrapper.example;

import java.util.List;

import de.zalando.sprocwrapper.SProcCall;
import de.zalando.sprocwrapper.SProcParam;
import de.zalando.sprocwrapper.SProcService;
import de.zalando.sprocwrapper.sharding.ShardKey;
import de.zalando.sprocwrapper.sharding.VirtualShardIdentityStrategy;

@SProcService
public interface ShardingSprocService {

    @SProcCall(shardStrategy = VirtualShardIdentityStrategy.class)
    int getShardIndex(@ShardKey int shard);

    @SProcCall(runOnAllShards = true)
    List<String> collectDataFromAllShards(@SProcParam String someParameter);

    @SProcCall(runOnAllShards = true, parallel = true, name = "collect_data_from_all_shards")
    List<String> collectDataFromAllShardsParallel(@SProcParam String someParameter);

    @SProcCall(runOnAllShards = true, searchShards = true)
    List<String> collectDataFromAllShardsSearchShardsOn(@SProcParam String someParameter);

    @SProcCall(
        runOnAllShards = true, parallel = true, searchShards = true,
        name = "collect_data_from_all_shards_search_shards_on"
    )
    List<String> collectDataFromAllShardsParallelSearchShardsOn(@SProcParam String someParameter);

}
