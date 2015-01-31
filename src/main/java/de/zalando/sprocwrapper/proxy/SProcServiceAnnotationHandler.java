package de.zalando.sprocwrapper.proxy;

import de.zalando.sprocwrapper.SProcService;
import de.zalando.sprocwrapper.sharding.VirtualShardKeyStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by soroosh on 1/31/15.
 */
class SProcServiceAnnotationHandler {
    private static final Logger LOG = LoggerFactory.getLogger(SProcServiceAnnotationHandler.class);
    private static final String DEFAULT_PREFIX = "";
    private static final VirtualShardKeyStrategy VIRTUAL_SHARD_KEY_STRATEGY_DEFAULT = new VirtualShardKeyStrategy();

    protected static final HandlerResult DEFAULT_HANDLER_RESULT = new HandlerResult(DEFAULT_PREFIX, VIRTUAL_SHARD_KEY_STRATEGY_DEFAULT);

    public static class HandlerResult {

        private final String prefix;
        private final VirtualShardKeyStrategy shardKeyStrategy;

        public HandlerResult(String prefix, VirtualShardKeyStrategy shardKeyStrategy) {

            this.prefix = prefix;
            this.shardKeyStrategy = shardKeyStrategy;
        }

        public String getPrefix() {
            return prefix;
        }

        public VirtualShardKeyStrategy getShardKeyStrategy() {
            return shardKeyStrategy;
        }

    }

    public SProcServiceAnnotationHandler() {

    }

    public HandlerResult handle(final Class<?> c) {
        SProcService serviceAnnotation = c.getAnnotation(SProcService.class);
        if (serviceAnnotation == null) {
            return DEFAULT_HANDLER_RESULT;
        }
        VirtualShardKeyStrategy keyStrategy = VIRTUAL_SHARD_KEY_STRATEGY_DEFAULT;

        try {
            keyStrategy = (VirtualShardKeyStrategy) serviceAnnotation.shardStrategy().newInstance();
        } catch (final InstantiationException | IllegalAccessException ex) {
            LOG.error("ShardKey strategy for service can not be instantiated", ex);
            throw new IllegalArgumentException("ShardKey strategy for service can not be instantiated");
        }
        String prefix = DEFAULT_PREFIX;
        if (!"".equals(serviceAnnotation.namespace().trim())) {
            prefix = serviceAnnotation.namespace() + "_";
        }
        return new HandlerResult(prefix, keyStrategy);
    }
}
