package org.zalando.sprocwrapper.sharding;

/**
 * @author  hjacobs
 */
public interface ShardedObject {

    Object getShardKey();
}
