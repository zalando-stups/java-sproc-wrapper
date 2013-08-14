package de.zalando.typemapper.postgres;

import java.sql.Connection;

abstract class AbstractPgSerializer {

    static final String NULL = "NULL";
    static final char DOUBLE_QUOTE = '"';
    static final char BACKSLASH = '\\';
    static final char COMMA = ',';

    private String value;

    public abstract boolean isNull();

    protected abstract boolean isEmpty();

    protected abstract String getEmpty();

    protected abstract void appendNull(StringBuilder sb);

    public abstract StringBuilder quote(final StringBuilder sb, final CharSequence s);

    public abstract String toPgString(Connection connection);

    public final String toString(final Connection connection) {
        if (isNull()) {
            throw new NullPointerException("Null value of depends on the context, should use isNull() call before");
        }

        if (value == null) {
            if (isEmpty()) {
                value = getEmpty();
                return value;
            }

            value = toPgString(connection);
        }

        return value;
    }

    @Override
    public final String toString() {
        return toString(null);
    }
}
