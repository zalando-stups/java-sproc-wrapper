
package de.zalando.sprocwrapper.example;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Repository;

import de.zalando.sprocwrapper.AbstractSProcService;
import de.zalando.sprocwrapper.dsprovider.BitmapShardDataSourceProvider;

@Repository
public class ExampleBitmapShardSProcServiceImpl
    extends AbstractSProcService<ExampleBitmapShardSProcService, BitmapShardDataSourceProvider>
    implements ExampleBitmapShardSProcService {

    @Autowired
    public ExampleBitmapShardSProcServiceImpl(
            @Qualifier("testBitmapShardDataSourceProvider") final BitmapShardDataSourceProvider p) {
        super(p, ExampleBitmapShardSProcService.class);
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
    public Integer searchSomethingOnShards(final String someParameter) {
        return sproc.searchSomethingOnShards(someParameter);
    }

    @Override
    public List<Integer> searchSomethingElseOnShards(final String someParameter) {
        return sproc.searchSomethingElseOnShards(someParameter);
    }

    @Override
    public String getShardName(final int shard) {
        return sproc.getShardName(shard);
    }

    @Override
    public List<String> collectDataUsingAutoPartition(final List<String> keys) {
        return sproc.collectDataUsingAutoPartition(keys);
    }

    @Override
    public List<String> collectDataUsingAutoPartition2(final List<ExampleShardedObject> keys,
            final int additionalParam) {
        return sproc.collectDataUsingAutoPartition2(keys, additionalParam);
    }

    @Override
    public List<String> insertNewDataWithoutTransaction(final String someData, final String failOnShard) {
        return sproc.insertNewDataWithoutTransaction(someData, failOnShard);
    }

    @Override
    public List<AddressPojo> findAddressesByStreet(final String string) {
        return sproc.findAddressesByStreet(string);
    }

    @Override
    public List<String> insertAddress(final String someData, final String failOnShard) {
        return sproc.insertAddress(someData, failOnShard);
    }

    @Override
    public List<String> insertAddressOnePhase(final String someData, final String failOnShard) {
        return sproc.insertAddressOnePhase(someData, failOnShard);
    }

    @Override
    public List<String> insertAddressTwoPhase(final String someData, final String failOnShard) {
        return sproc.insertAddressTwoPhase(someData, failOnShard);
    }

    @Override
    public List<String> insertAddressUseFromService(final String someData, final String failOnShard) {
        return sproc.insertAddressUseFromService(someData, failOnShard);
    }

    @Override
    public List<String> insertAddressTwoPhaseReadOnly(final String someData, final String failOnShard) {
        return sproc.insertAddressTwoPhaseReadOnly(someData, failOnShard);
    }

    @Override
    public List<String> insertAddressParallel(final String someData, final String failOnShard) {
        return sproc.insertAddress(someData, failOnShard);
    }

    @Override
    public List<String> insertAddressOnePhaseParallel(final String someData, final String failOnShard) {
        return sproc.insertAddressOnePhase(someData, failOnShard);
    }

    @Override
    public List<String> insertAddressTwoPhaseParallel(final String someData, final String failOnShard) {
        return sproc.insertAddressTwoPhase(someData, failOnShard);
    }

    @Override
    public List<String> insertAddressUseFromServiceParallel(final String someData, final String failOnShard) {
        return sproc.insertAddressUseFromService(someData, failOnShard);
    }

    @Override
    public List<String> insertAddressTwoPhaseReadOnlyParallel(final String someData, final String failOnShard) {
        return sproc.insertAddressTwoPhaseReadOnly(someData, failOnShard);
    }
}
