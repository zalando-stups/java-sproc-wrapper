package de.zalando.typemapper.postgres;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.Arrays;
import java.util.Collection;

import org.postgresql.util.PGobject;

public final class PgRow extends PGobject {

    private static final long serialVersionUID = -2855096142894174113L;

    private final PgRowSerializer serializer;

    private final class PgRowSerializer extends AbstractPgCollectionSerializer<Object> {
        protected PgRowSerializer(final Collection<Object> c) {
            super(c);
        }

        @Override
        protected char getOpeningChar() {
            return '(';
        }

        @Override
        protected char getClosingChar() {
            return ')';
        }

        @Override
        protected void quoteChar(final StringBuilder sb, final char ch) {
            sb.append(ch).append(ch);
        }

        @Override
        protected void appendNull(final StringBuilder sb) {
            // do nothing as NULL is serialized as nothing for ROW
        }
    }

    protected PgRow(final String recordTypeName, final Collection<Object> c) throws SQLException {
        this.serializer = new PgRowSerializer(c);
        this.setType(recordTypeName);
        this.setValue(serializer.toString());
    }

    public PgRow(final PgTypeHelper.PgTypeDataHolder typeDataHolder) throws SQLException {
        this(typeDataHolder.getTypeName(), typeDataHolder.getAttributes());
    }

    public PgRow(final PgTypeHelper.PgTypeDataHolder typeDataHolder, final Connection connection) throws SQLException {
        this.serializer = new PgRowSerializer(typeDataHolder.getAttributes());
        this.setType(typeDataHolder.getTypeName());
        this.setValue(serializer.toString(connection));
    }

    public static PgRow ROW(final Object... array) throws SQLException {
        return new PgRow(null, array == null ? null : Arrays.asList(array));
    }

    @SuppressWarnings("unchecked")
    public static PgRow ROW(final Collection<?> collection) throws SQLException {
        return new PgRow(null, (Collection<Object>) collection);
    }

    @Override
    public String toString() {
        return serializer.toString();
    }

    public PGobject asPGobject(final String recordTypeName) throws SQLException {
        this.setType(recordTypeName);
        return this;
    }

}
