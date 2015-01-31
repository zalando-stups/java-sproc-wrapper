package de.zalando.sprocwrapper.proxy;

import de.zalando.sprocwrapper.SProcCall;
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
    private static final VirtualShardKeyStrategy DEFAULT_VIRTUAL_SHARD_KEY_STRATEGY = new VirtualShardKeyStrategy();

    protected static final HandlerResult DEFAULT_HANDLER_RESULT = new HandlerResult(DEFAULT_PREFIX, DEFAULT_VIRTUAL_SHARD_KEY_STRATEGY, false, SProcService.WriteTransaction.NONE);

    public static class HandlerResult {

        private final String prefix;
        private final VirtualShardKeyStrategy shardKeyStrategy;
        private boolean validationActive;
        private SProcService.WriteTransaction writeTransaction;

        public HandlerResult(String prefix, VirtualShardKeyStrategy shardKeyStrategy, boolean validationActive, SProcService.WriteTransaction writeTransaction) {

            this.prefix = prefix;
            this.shardKeyStrategy = shardKeyStrategy;
            this.validationActive = validationActive;
            this.writeTransaction = writeTransaction;
        }

        public String getPrefix() {
            return prefix;
        }

        public VirtualShardKeyStrategy getShardKeyStrategy() {
            return shardKeyStrategy;
        }

        public boolean isValidationActive() {
            return validationActive;
        }

        public SProcService.WriteTransaction getWriteTransaction() {
            return this.writeTransaction;
        }
    }

    public SProcServiceAnnotationHandler() {

    }

    public HandlerResult handle(final Class<?> c) {
        SProcService serviceAnnotation = c.getAnnotation(SProcService.class);
        if (serviceAnnotation == null) {
            return DEFAULT_HANDLER_RESULT;
        }
        VirtualShardKeyStrategy keyStrategy = DEFAULT_VIRTUAL_SHARD_KEY_STRATEGY;

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

        return new HandlerResult(prefix, keyStrategy, serviceAnnotation.validate(), serviceAnnotation.shardedWriteTransaction());
    }
}
