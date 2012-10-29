package de.zalando.sprocwrapper.sharding;

/**
 * extract the encoded shard bits from a long shard-aware ID.
 *
 * @author  jmussler
 * @author  hjacobs
 */
public class VirtualShardAwareIdStrategy extends VirtualShardKeyStrategy {

    @Override
    public int getShardId(final Object[] objs) {
        if (objs == null || objs.length == 0) {
            return 0;
        }

        if (objs[0] == null) {
            return 0;
        }

        long id = (objs[0] instanceof Long ? (Long) objs[0] : (Integer) objs[0]);

        // 10 lower bits represent sequence number
        // extract the next 9 bit as virtual shard id
        return (int) (id >> 10) & 0x1ff;
    }
}
