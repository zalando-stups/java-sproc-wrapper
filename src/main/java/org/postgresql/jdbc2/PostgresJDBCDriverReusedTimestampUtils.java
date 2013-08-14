package org.postgresql.jdbc2;

/**
 * Helper class to reuse TimestampUtils from the postgres jdbc-driver. Must be placed in org.postgresql.jdbc2 package
 * because of package based constructor
 *
 * @author  wolters
 */
public class PostgresJDBCDriverReusedTimestampUtils extends TimestampUtils {
    public PostgresJDBCDriverReusedTimestampUtils() {
        super(true, true);
    }
}
