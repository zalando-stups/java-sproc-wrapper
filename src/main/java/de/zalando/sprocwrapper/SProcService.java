package de.zalando.sprocwrapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.zalando.sprocwrapper.sharding.VirtualShardKeyStrategy;

/**
 * @author  jmussler
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface SProcService {
    public enum WriteTransaction {
        NONE,
        ONE_PHASE,
        TWO_PHASE
    }

    Class<?> shardStrategy() default VirtualShardKeyStrategy.class;

    String namespace() default "";

    boolean validate() default false;

    /**
     * Defines how sharded writes will be handled. If set to {@link WriteTransaction#NONE}, no transaction context will
     * be created. If set to {@link WriteTransaction#ONE_PHASE}, all errors during the sproc call will be rolled back.
     * If set to {@link WriteTransaction#TWO_PHASE}, all errors during sproc call and "prepare transaction" are rolled
     * back. In the last case, the Postgres instance must be configured to manage 2-phase-commits (XA).
     */
    WriteTransaction shardedWriteTransaction() default WriteTransaction.NONE;
}
