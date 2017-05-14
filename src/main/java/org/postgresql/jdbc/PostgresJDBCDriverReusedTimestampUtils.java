package org.postgresql.jdbc;

import org.postgresql.core.Provider;

import java.util.TimeZone;

/**
 * Helper class to reuse TimestampUtils from the postgres jdbc-driver. Must be placed in org.postgresql.jdbc2 package
 * because of package based constructor
 *
 * @author wolters
 */
public class PostgresJDBCDriverReusedTimestampUtils extends TimestampUtils {
    public PostgresJDBCDriverReusedTimestampUtils() {
        super(false, new Provider<TimeZone>() {
            @Override
            public TimeZone get() {
                return TimeZone.getDefault();
            }
        });
    }
}
