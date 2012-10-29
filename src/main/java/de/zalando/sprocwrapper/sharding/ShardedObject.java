package de.zalando.sprocwrapper.sharding;

/**
 * @author  hjacobs
 */
public interface ShardedObject {

    Object getShardKey();
}
