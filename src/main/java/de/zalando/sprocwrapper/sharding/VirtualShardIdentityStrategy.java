package de.zalando.sprocwrapper.sharding;

/**
 * simple strategy were the shard key is returned unmodified as integer. (this allows for "@ShardKey int shardIndex"
 * pseudo parameters)
 *
 * @author  hjacobs
 */
public class VirtualShardIdentityStrategy extends VirtualShardKeyStrategy {

    /**
     * @param   objs  Key Objects
     *
     * @return  virtual shard id
     */
    @Override
    public int getShardId(final Object[] objs) {
        return (Integer) objs[0];
    }
}
