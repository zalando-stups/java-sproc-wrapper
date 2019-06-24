package org.zalando.sprocwrapper;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zalando.sprocwrapper.example.ExampleBitmapShardSProcService;
import org.zalando.sprocwrapper.example.ExampleShardedObject;
import org.zalando.sprocwrapper.example.ShardingSprocService;

import java.util.List;

/**
 * @author  hjacobs
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:backendContextTest.xml"})
public class ShardingIT {

    @Autowired
    private ShardingSprocService shardingSProcService;

    @Autowired
    private ExampleBitmapShardSProcService exampleBitmapShardSProcService;

    @Test
    public void testSharding() {

        // test simple identity + modulo sharding strategy
        Assert.assertEquals(0, shardingSProcService.getShardIndex(122));
        Assert.assertEquals(1, shardingSProcService.getShardIndex(123));
    }

    @Test
    public void testRunOnAllShards() {

        final List<String> results = shardingSProcService.collectDataFromAllShards("a");
        Assert.assertEquals(4, results.size());
        Assert.assertEquals("shard1row1", results.get(0));
        Assert.assertEquals("shard2row1", results.get(2));
        Assert.assertEquals("shard2row2", results.get(3));
    }

    @Test
    public void testRunOnAllShardsParallel() {

        final List<String> results = shardingSProcService.collectDataFromAllShardsParallel("a");
        Assert.assertEquals(4, results.size());
        Assert.assertEquals("shard1row1", results.get(0));
        Assert.assertEquals("shard2row1", results.get(2));
        Assert.assertEquals("shard2row2", results.get(3));

        shardingSProcService.collectDataFromAllShardsParallel("a");
    }

    @Test
    public void testRunOnAllShardsSearchShardsOn() {

        // Search should stop on first shard with results.
        final List<String> results = shardingSProcService.collectDataFromAllShardsSearchShardsOn("a");
        Assert.assertEquals(2, results.size());
        Assert.assertEquals("shard2row1", results.get(0));
        Assert.assertEquals("shard2row2", results.get(1));
    }

    @Test
    public void testRunOnAllShardsParallelSearchShardsOn() {

        // Search should stop on first shard with results.
        final List<String> results = shardingSProcService.collectDataFromAllShardsParallelSearchShardsOn("a");
        Assert.assertEquals(2, results.size());
        Assert.assertEquals("shard2row1", results.get(0));
        Assert.assertEquals("shard2row2", results.get(1));
    }

    @Test
    public void testSearchShards() {

        Integer result = exampleBitmapShardSProcService.searchSomethingOnShards("X");
        Assert.assertNull(result);

        result = exampleBitmapShardSProcService.searchSomethingOnShards("A");
        Assert.assertEquals(1, (int) result);

        result = exampleBitmapShardSProcService.searchSomethingOnShards("B");
        Assert.assertEquals(2, (int) result);

        final List<Integer> result2 = exampleBitmapShardSProcService.searchSomethingElseOnShards("UNUSED");
        Assert.assertEquals(1, result2.size());
    }

    @Test
    public void testBitmapShards() {
        Assert.assertEquals(0, exampleBitmapShardSProcService.getShardIndex(0));
        Assert.assertEquals(1, exampleBitmapShardSProcService.getShardIndex(1));
        Assert.assertEquals(0, exampleBitmapShardSProcService.getShardIndex(124));
        Assert.assertEquals(1, exampleBitmapShardSProcService.getShardIndex(125));

        Assert.assertEquals("00", exampleBitmapShardSProcService.getShardName(0));
        Assert.assertEquals("1", exampleBitmapShardSProcService.getShardName(1));
        Assert.assertEquals("10", exampleBitmapShardSProcService.getShardName(2));
        Assert.assertEquals("1", exampleBitmapShardSProcService.getShardName(3));
    }

    @Test
    public void testAutoPartitioning() {

        List<String> keys = Lists.newArrayList("a");

        // config in backendContextText.xml:
        // 00 -> shard 1
        // 10 -> shard 1
        // 01 -> shard 2
        // 11 -> shard 2

        // MD5(a), last 3 byte => 0x772661 => 7808609 => binary endswith 1001 =>
        // shard 2
        // MD5(b), last 3 byte => 0x31578f => 3233679 => binary endswith 1001 =>
        // shard 2
        // MD5(c), last 3 byte => 0x8b5f33 => 9133875 => binary endswith 0101 =>
        // shard 2
        // MD5(d), last 3 byte => 0xe091ad => 14717357 => binary endswith 0111
        // => shard 2
        // MD5(e), last 3 byte => 0x41ec32 => 4320306 => binary endswith 0110 =>
        // shard 1
        List<String> results = exampleBitmapShardSProcService.collectDataUsingAutoPartition(keys);
        Assert.assertEquals(2, results.size());
        Assert.assertEquals("shard2row1a", results.get(0));
        Assert.assertEquals("shard2row2a", results.get(1));

        keys = Lists.newArrayList("e");
        results = exampleBitmapShardSProcService.collectDataUsingAutoPartition(keys);
        Assert.assertEquals(2, results.size());
        Assert.assertEquals("shard1row1e", results.get(0));
        Assert.assertEquals("shard1row2e", results.get(1));

        keys = Lists.newArrayList("a", "b", "c", "d", "e");
        results = exampleBitmapShardSProcService.collectDataUsingAutoPartition(keys);
        Assert.assertEquals(4, results.size());
        Assert.assertEquals("shard1row1e", results.get(0));
        Assert.assertEquals("shard1row2e", results.get(1));
        Assert.assertEquals("shard2row1a", results.get(2));
        Assert.assertEquals("shard2row2a", results.get(3));

        final List<ExampleShardedObject> objectKeys = Lists.newArrayList(new ExampleShardedObject("a", "b"),
                new ExampleShardedObject("c", "d"), new ExampleShardedObject("e", "f"));
        results = exampleBitmapShardSProcService.collectDataUsingAutoPartition2(objectKeys, 3);
        Assert.assertEquals(2, results.size());
        Assert.assertEquals("shard1row1ef3", results.get(0));
        Assert.assertEquals("shard2row1ab3", results.get(1));

    }
}
