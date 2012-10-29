package de.zalando.sprocwrapper.example;

import java.util.List;

import de.zalando.sprocwrapper.SProcCall;
import de.zalando.sprocwrapper.SProcCall.WriteTransaction;
import de.zalando.sprocwrapper.SProcParam;
import de.zalando.sprocwrapper.SProcService;
import de.zalando.sprocwrapper.sharding.ShardKey;
import de.zalando.sprocwrapper.sharding.VirtualShardIdentityStrategy;
import de.zalando.sprocwrapper.sharding.VirtualShardMd5Strategy;

/**
 * @author  jmussler
 */
@SProcService
public interface ExampleBitmapShardSProcService {

    @SProcCall(shardStrategy = VirtualShardIdentityStrategy.class)
    int getShardIndex(@ShardKey final int shard);

    @SProcCall(runOnAllShards = true)
    List<String> collectDataFromAllShards(@SProcParam final String someParameter);

    @SProcCall(searchShards = true)
    Integer searchSomethingOnShards(@SProcParam final String someParameter);

    @SProcCall(searchShards = true)
    List<Integer> searchSomethingElseOnShards(@SProcParam final String someParameter);

    @SProcCall(shardStrategy = VirtualShardIdentityStrategy.class, sql = "SELECT shard_name FROM shard_name")
    String getShardName(@ShardKey final int shard);

    @SProcCall(shardStrategy = VirtualShardMd5Strategy.class)
    List<String> collectDataUsingAutoPartition(@ShardKey @SProcParam final List<String> keys);

    @SProcCall(shardStrategy = VirtualShardMd5Strategy.class, name = "collect_data_using_auto_partition2")
    List<String> collectDataUsingAutoPartition2(
            @ShardKey
            @SProcParam(type = "example_sharded_object[]")
            final List<ExampleShardedObject> keys, @SProcParam final int additionalParam);

    @SProcCall(runOnAllShards = true, readOnly = false, shardedWriteTransaction = WriteTransaction.NONE)
    List<String> insertAddress(@SProcParam final String someData, @SProcParam final String failOnShard);

    @SProcCall(
        name = "insert_address", runOnAllShards = true, readOnly = false,
        shardedWriteTransaction = WriteTransaction.ONE_PHASE
    )
    List<String> insertAddressOnePhase(@SProcParam final String someData, @SProcParam final String failOnShard);

    @SProcCall(
        name = "insert_address", runOnAllShards = true, readOnly = false,
        shardedWriteTransaction = WriteTransaction.TWO_PHASE
    )
    List<String> insertAddressTwoPhase(@SProcParam final String someData, @SProcParam final String failOnShard);

    @SProcCall(
        name = "insert_address", runOnAllShards = true, readOnly = false,
        shardedWriteTransaction = WriteTransaction.USE_FROM_SERVICE
    )
    List<String> insertAddressUseFromService(@SProcParam final String someData, @SProcParam final String failOnShard);

    @SProcCall(
        name = "insert_address", runOnAllShards = true, readOnly = true,
        shardedWriteTransaction = WriteTransaction.TWO_PHASE
    )
    List<String> insertAddressTwoPhaseReadOnly(@SProcParam final String someData, @SProcParam final String failOnShard);

    // the sproc mapper does not check if the sproc itself will only do reading access.
    //
    @SProcCall(runOnAllShards = true, readOnly = true)
    List<String> insertNewDataWithoutTransaction(@SProcParam final String someData,
            @SProcParam final String failOnShard);

    @SProcCall(
        name = "insert_address", runOnAllShards = true, readOnly = false,
        shardedWriteTransaction = WriteTransaction.NONE, parallel = true
    )
    List<String> insertAddressParallel(@SProcParam final String someData, @SProcParam final String failOnShard);

    @SProcCall(
        name = "insert_address", runOnAllShards = true, readOnly = false,
        shardedWriteTransaction = WriteTransaction.ONE_PHASE, parallel = true
    )
    List<String> insertAddressOnePhaseParallel(@SProcParam final String someData, @SProcParam final String failOnShard);

    @SProcCall(
        name = "insert_address", runOnAllShards = true, readOnly = false,
        shardedWriteTransaction = WriteTransaction.TWO_PHASE, parallel = true
    )
    List<String> insertAddressTwoPhaseParallel(@SProcParam final String someData, @SProcParam final String failOnShard);

    @SProcCall(
        name = "insert_address", runOnAllShards = true, readOnly = false,
        shardedWriteTransaction = WriteTransaction.USE_FROM_SERVICE, parallel = true
    )
    List<String> insertAddressUseFromServiceParallel(@SProcParam final String someData,
            @SProcParam final String failOnShard);

    @SProcCall(
        name = "insert_address", runOnAllShards = true, readOnly = true,
        shardedWriteTransaction = WriteTransaction.TWO_PHASE, parallel = true
    )
    List<String> insertAddressTwoPhaseReadOnlyParallel(@SProcParam final String someData,
            @SProcParam final String failOnShard);

    @SProcCall(runOnAllShards = true, readOnly = true)
    List<AddressPojo> findAddressesByStreet(@SProcParam final String string);

}
