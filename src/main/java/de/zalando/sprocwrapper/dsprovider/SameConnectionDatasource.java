package de.zalando.sprocwrapper.dsprovider;

import java.io.PrintWriter;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * This class is supposed to wrap a single connection for a single thread. It does not checking nor is it thread-safe
 * for get and close.
 *
 * @author  jmussler
 */
public class SameConnectionDatasource implements DataSource {

    private final ConnectionWrapper connection;
    private boolean closed = false;

    public SameConnectionDatasource(final Connection conn) {
        connection = new ConnectionWrapper(conn);
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (closed) {
            throw new SQLException("Datasource is already closed!!");
        }

        return connection;
    }

    public void close() throws SQLException {
        closed = true;
        connection.propagateClose();
    }

    @Override
    public Connection getConnection(final String string, final String string1) throws SQLException {
        return getConnection();
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setLogWriter(final PrintWriter writer) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setLoginTimeout(final int i) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> T unwrap(final Class<T> type) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isWrapperFor(final Class<?> type) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("Not supported yet.");
    }
}
