package de.zalando.sprocwrapper.proxy;

import de.zalando.sprocwrapper.SProcService;
import de.zalando.sprocwrapper.sharding.VirtualShardKeyStrategy;

/**
 * Created by soroosh on 1/31/15.
 */
class SProcServiceAnnotationHandler {
    protected static final VirtualShardKeyStrategy VIRTUAL_SHARD_KEY_STRATEGY_DEFAULT = new VirtualShardKeyStrategy();
    protected static final HandlerResult DEFAULT_HANDLER_RESULT = new HandlerResult("", VIRTUAL_SHARD_KEY_STRATEGY_DEFAULT);

    public static class HandlerResult {
        private final String prefix;
        private final VirtualShardKeyStrategy shardKeyStrategy;

        public HandlerResult(String prefix, VirtualShardKeyStrategy shardKeyStrategy) {

            this.prefix = prefix;
            this.shardKeyStrategy = shardKeyStrategy;
        }

    }

    public SProcServiceAnnotationHandler() {

    }

    public HandlerResult handle(final Class<?> c) {
        SProcService annotation = c.getAnnotation(SProcService.class);
        if (annotation == null){
            return DEFAULT_HANDLER_RESULT;
        }
        return new HandlerResult("", null);
    }
}
