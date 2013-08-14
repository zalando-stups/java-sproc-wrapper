package de.zalando.sprocwrapper.sharding;

import java.util.Map;

import org.springframework.dao.DataAccessException;

import com.google.common.collect.ImmutableMap;

/**
 * Signals that at an exception has occurred while executing the sproc on the shards.
 *
 * @author  pribeiro
 */
public class ShardedDataAccessException extends DataAccessException {

    private final Map<Integer, Throwable> causes;

    /**
     * Creates a {@code ShardedDataAccessException} with specified detail message and causes.
     *
     * @param  msg     the detail message
     * @param  causes  the causes
     */
    public ShardedDataAccessException(final String msg, final Map<Integer, Throwable> causes) {
        super(msg);

        // create an immutable map, no one should be able to change the causes.
        // Performance hint. Pass an immutable map into the constructor, so no copy is performed at all
        this.causes = (causes == null ? ImmutableMap.<Integer, Throwable>of() : ImmutableMap.copyOf(causes));

        // add causes to new java 7 suppressed exceptions feature so we can see all nested exceptions in stacktrace
        for (final Throwable cause : this.causes.values()) {
            addSuppressed(cause);
        }
    }

    /**
     * Returns an immutable map with the shard id and respective cause.
     *
     * @return  an immutable map with the shard id and respective cause
     */
    public Map<Integer, Throwable> getCauses() {
        return causes;
    }

}
