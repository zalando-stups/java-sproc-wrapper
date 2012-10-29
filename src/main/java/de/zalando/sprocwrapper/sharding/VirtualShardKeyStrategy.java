package de.zalando.sprocwrapper.sharding;

/**
 * @author  jmussler
 */
public class VirtualShardKeyStrategy {

    /**
     * @param   objs  Key Objects
     *
     * @return  virtual shard id
     */
    public int getShardId(final Object[] objs) {
        return 0;
    }
}
