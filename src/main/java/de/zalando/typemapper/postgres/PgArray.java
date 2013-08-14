package de.zalando.typemapper.postgres;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public final class PgArray<E> implements java.sql.Array {

    private String elementTypeName = null;
    private final PgArraySerializer<E> serializer;
    private String serializedString = null;

    private static class PgArraySerializer<E> extends AbstractPgCollectionSerializer<E> {

        protected PgArraySerializer(final Collection<E> c) {
            super(c);
        }

        @Override
        protected char getOpeningChar() {
            return '{';
        }

        @Override
        protected char getClosingChar() {
            return '}';
        }

        @Override
        protected void quoteChar(final StringBuilder sb, final char ch) {
            sb.append(AbstractPgSerializer.BACKSLASH).append(ch);
        }

        @Override
        protected void appendNull(final StringBuilder sb) {
            sb.append(AbstractPgSerializer.NULL);
        }
    }

    protected PgArray(final String elementTypeName, final Collection<E> c) {
        this.serializer = new PgArraySerializer<E>(c);
        this.elementTypeName = elementTypeName;
    }

    public static <T> PgArray<T> ARRAY(final T... array) {
        return PgArray.ARRAY(array == null ? null : Arrays.asList(array));
    }

    public static <T> PgArray<T> ARRAY(final Collection<T> collection) {

        // try to find out the element type of the collection
        Class<?> elementClass = null;
        String type = null;
        for (final T element : collection) {
            if (element != null) {
                elementClass = element.getClass();

                // check if the collection element is of type PgRow (complex type)
                // in that case take the database type from the PgRow object.
                if (PgRow.class.isAssignableFrom(elementClass)) {
                    type = ((PgRow) element).getType();
                }

                break;
            }
        }

        if (type != null) {
            return new PgArray<T>(type, collection);
        } else {

            return new PgArray<T>(PgTypeHelper.getSQLNameForClass(elementClass), collection);
        }
    }

    @Override
    public String toString() {
        if (serializedString != null) {
            return serializedString;
        }

        return serializer.toString();
    }

    public String toString(final Connection connection) {
        return serializer.toString(connection);
    }

    /**
     * Returns a java.sql.Array view with the specified element type name for the current PgArray object.
     *
     * @param   elementTypeName
     *
     * @return  java.sql.Array view with the specified element type name
     */
    public java.sql.Array asJdbcArray(final String elementTypeName) {
        this.elementTypeName = elementTypeName;
        return this;
    }

    public java.sql.Array asJdbcArray(final String elementTypeName, final Connection connection) {
        this.elementTypeName = elementTypeName;
        this.serializedString = serializer.toString(connection);
        return this;
    }

    @Override
    public String getBaseTypeName() throws SQLException {
        if (elementTypeName == null) {
            throw new SQLException(
                "Array element type is not defined, use asJdbcArray(<elementTypeName>) to get a valid java.sql.Array object");
        }

        return elementTypeName;
    }

    @Override
    public int getBaseType() throws SQLException {
        if (elementTypeName == null) {
            throw new SQLException(
                "Array element type is not defined, use asJdbcArray(<elementTypeName>) to get a valid java.sql.Array object");
        }

        return PgTypeHelper.getSQLType(elementTypeName);
    }

    @Override
    public Object getArray() throws SQLException {
        return serializer.collection.toArray();
    }

    @Override
    public Object getArray(final Map<String, Class<?>> map) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }

    @Override
    public Object getArray(final long index, final int count) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }

    @Override
    public Object getArray(final long index, final int count, final Map<String, Class<?>> map) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }

    @Override
    public ResultSet getResultSet(final Map<String, Class<?>> map) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }

    @Override
    public ResultSet getResultSet(final long index, final int count) throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }

    @Override
    public ResultSet getResultSet(final long index, final int count, final Map<String, Class<?>> map)
        throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }

    @Override
    public void free() throws SQLException {
        throw new SQLFeatureNotSupportedException("Feature not supported");
    }

}
