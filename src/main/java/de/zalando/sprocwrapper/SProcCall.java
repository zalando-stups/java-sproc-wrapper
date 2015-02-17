package de.zalando.sprocwrapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author jmussler
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface SProcCall {

    public static enum AdvisoryLock {
        NO_LOCK(0L),
        LOCK_ONE(1L);

        /*
         * Add more values to this enum if you need additional types of locks
         */

        private AdvisoryLock(final long sprocId) {
            this.sprocId = sprocId;
        }

        private final long sprocId;

        public long getSprocId() {
            return sprocId;
        }

    }

    public static enum Validate {
        AS_DEFINED_IN_SERVICE,
        YES,
        NO
    }

    public static enum WriteTransaction {
        USE_FROM_SERVICE,
        NONE,
        ONE_PHASE,
        TWO_PHASE

    }

    String name() default "";

    String sql() default "";

    Class<?> shardStrategy() default Void.class;

    /**
     * whether the stored procedure should be called on all shards --- results are concatenated together.
     *
     * @return
     */
    boolean runOnAllShards() default false;

    /**
     * whether the stored procedure should be called on all shards --- return the first result found.
     *
     * @return
     */
    boolean searchShards() default false;

    /**
     * run sproc on multiple shards in parallel?
     *
     * @return
     */
    boolean parallel() default false;

    /**
     * flag this stored procedure call as read only: read only sprocs may run in cases were writing calls would not be
     * allowed (maintenance, migration, ..)
     *
     * @return
     */
    boolean readOnly() default true;

    /**
     * Defines how sharded writes will be handled. If set to {@link WriteTransaction#NONE}, no transaction context will
     * be created. If set to {@link WriteTransaction#ONE_PHASE}, all errors during the sproc call will be rolled back.
     * If set to {@link WriteTransaction#TWO_PHASE}, all errors during sproc call and "prepare transaction" are rolled
     * back. In the last case, the Postgres instance must be configured to manage 2-phase-commits (XA).
     */
    WriteTransaction shardedWriteTransaction() default WriteTransaction.USE_FROM_SERVICE;

    Class<?> resultMapper() default Void.class;

    long timeoutInMilliSeconds() default 0;

    AdvisoryLock adivsoryLockType() default AdvisoryLock.NO_LOCK;

    Validate validate() default Validate.AS_DEFINED_IN_SERVICE;
}
