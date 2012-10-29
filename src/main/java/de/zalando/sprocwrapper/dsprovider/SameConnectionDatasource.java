package de.zalando.sprocwrapper.dsprovider;

import java.io.PrintWriter;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * @author  jmussler
 */
public class SameConnectionDatasource implements DataSource {

    private final ConnectionWrapper connection;
    private boolean closed = false;

    public SameConnectionDatasource(final Connection conn) {
        connection = new ConnectionWrapper(conn);
    }

    @Override
    public synchronized Connection getConnection() throws SQLException {
        if (closed) {
            throw new SQLException("Datasource is already closed!!");
        }

        return connection;
    }

    public synchronized void close() throws SQLException {
        closed = true;
        connection.propagateClose();
    }

    @Override
    public synchronized Connection getConnection(final String string, final String string1) throws SQLException {
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
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
