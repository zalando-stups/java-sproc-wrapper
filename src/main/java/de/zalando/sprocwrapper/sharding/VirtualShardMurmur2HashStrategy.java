package de.zalando.sprocwrapper.sharding;

import java.util.List;

public class VirtualShardMurmur2HashStrategy extends VirtualShardKeyStrategy {

    /**
     * @param   objs  Key Objects
     *
     * @return  virtual shard id
     */
    @Override
    public int getShardId(final Object[] objs) {
        if (objs == null || objs.length == 0) {
            return 0;
        }

        Object input = null;

        if (objs[0] == null) {
            return 0;
        }

        if (objs[0] instanceof List) {
            List<?> list = (List<?>) objs[0];
            if (list.isEmpty()) {
                return 0;
            }

            input = list.get(0);
        } else {
            input = objs[0];
        }
/*
 *      if (input instanceof Long) {
 *          hasher.putLong((Long) input);
 *      } else if (input instanceof Integer) {
 *          hasher.putInt((Integer) input);
 *      } else {
 *          hasher.putString((String) input);
 *      }*/

        return Murmur2Hash.hash(((String) input).getBytes(), 0);
    }
}
