package org.zalando.sprocwrapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import java.util.Objects;

import com.google.common.base.Preconditions;

/**
 * @author jmussler
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface SProcCall {

    public static class AdvisoryLock {

        public static class NoLock {
            public static final String NAME = "NO_LOCK";
            public static final long LOCK_ID = 0L;
            public static final AdvisoryLock LOCK = new AdvisoryLock(NAME, LOCK_ID);
        }

        public static class LockOne {
            public static final String NAME = "LOCK_ONE";
            public static final long LOCK_ID = 1L;
            public static final AdvisoryLock LOCK = new AdvisoryLock(NAME, LOCK_ID);
        }

        private final String name;
        private final long lockId;

        public AdvisoryLock(final String name, final long lockId) {
            Preconditions.checkNotNull(name, "Name parameter cannot be null.");
            Preconditions.checkArgument(lockId == NoLock.LOCK_ID && Objects.equals(name, NoLock.NAME)
                    || lockId != NoLock.LOCK_ID && !Objects.equals(name, NoLock.NAME),
                "LockId parameter is different than %s (%s) but the name parameter was not changed: [name: %s, lockId: %s]",
                NoLock.LOCK_ID, NoLock.NAME, name, lockId);
            this.name = name;
            this.lockId = lockId;
        }

        public String getName() {
            return name;
        }

        public long getLockId() {
            return lockId;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            final AdvisoryLock that = (AdvisoryLock) o;

            if (lockId != that.lockId) {
                return false;
            }

            if (!name.equals(that.name)) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + (int) (lockId ^ (lockId >>> 32));
            return result;
        }

        @Override
        public String toString() {
            return "AdvisoryLock{" + "name='" + name + '\'' + ", lockId=" + lockId + '}';
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

    long adivsoryLockId() default AdvisoryLock.NoLock.LOCK_ID;

    String adivsoryLockName() default AdvisoryLock.NoLock.NAME;

    Validate validate() default Validate.AS_DEFINED_IN_SERVICE;

}
