package de.zalando.sprocwrapper.dsprovider;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * @author  jmussler
 */
public class ConnectionWrapper implements Connection {

    private Connection conn;

    private boolean closed = false;

    public ConnectionWrapper(final Connection c) {
        conn = c;
    }

    public void propagateClose() throws SQLException {
        conn.close();
        closed = true;
    }

    @Override
    public Statement createStatement() throws SQLException {
        return conn.createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(final String string) throws SQLException {
        return conn.prepareStatement(string);
    }

    @Override
    public CallableStatement prepareCall(final String string) throws SQLException {
        return conn.prepareCall(string);
    }

    @Override
    public String nativeSQL(final String string) throws SQLException {
        return conn.nativeSQL(string);
    }

    @Override
    public void setAutoCommit(final boolean bln) throws SQLException {
        conn.setAutoCommit(bln);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return conn.getAutoCommit();
    }

    @Override
    public void commit() throws SQLException {
        conn.commit();
    }

    @Override
    public void rollback() throws SQLException {
        conn.rollback();
    }

    @Override
    public void close() throws SQLException {
        closed = true;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return closed;
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return conn.getMetaData();
    }

    @Override
    public void setReadOnly(final boolean bln) throws SQLException {
        conn.setReadOnly(bln);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return conn.isReadOnly();
    }

    @Override
    public void setCatalog(final String string) throws SQLException {
        conn.setCatalog(string);
    }

    @Override
    public String getCatalog() throws SQLException {
        return conn.getCatalog();
    }

    @Override
    public void setTransactionIsolation(final int i) throws SQLException {
        conn.setTransactionIsolation(i);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return conn.getTransactionIsolation();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return conn.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        conn.clearWarnings();
    }

    @Override
    public Statement createStatement(final int i, final int i1) throws SQLException {
        return conn.createStatement(i, i1);
    }

    @Override
    public PreparedStatement prepareStatement(final String string, final int i, final int i1) throws SQLException {
        return conn.prepareStatement(string, i, i1);
    }

    @Override
    public CallableStatement prepareCall(final String string, final int i, final int i1) throws SQLException {
        return conn.prepareCall(string, i, i1);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return conn.getTypeMap();
    }

    @Override
    public void setTypeMap(final Map<String, Class<?>> map) throws SQLException {
        conn.setTypeMap(map);
    }

    @Override
    public void setHoldability(final int i) throws SQLException {
        conn.setHoldability(i);
    }

    @Override
    public int getHoldability() throws SQLException {
        return conn.getHoldability();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return conn.setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(final String string) throws SQLException {
        return conn.setSavepoint(string);
    }

    @Override
    public void rollback(final Savepoint svpnt) throws SQLException {
        conn.rollback(svpnt);
    }

    @Override
    public void releaseSavepoint(final Savepoint svpnt) throws SQLException {
        conn.releaseSavepoint(svpnt);
    }

    @Override
    public Statement createStatement(final int i, final int i1, final int i2) throws SQLException {
        return conn.createStatement(i1, i1, i2);
    }

    @Override
    public PreparedStatement prepareStatement(final String string, final int i, final int i1, final int i2)
        throws SQLException {
        return conn.prepareStatement(string, i, i1, i2);
    }

    @Override
    public CallableStatement prepareCall(final String string, final int i, final int i1, final int i2)
        throws SQLException {
        return conn.prepareCall(string, i, i1, i2);
    }

    @Override
    public PreparedStatement prepareStatement(final String string, final int i) throws SQLException {
        return conn.prepareStatement(string, i);
    }

    @Override
    public PreparedStatement prepareStatement(final String string, final int[] ints) throws SQLException {
        return conn.prepareStatement(string, ints);
    }

    @Override
    public PreparedStatement prepareStatement(final String string, final String[] strings) throws SQLException {
        return conn.prepareStatement(string, strings);
    }

    @Override
    public Clob createClob() throws SQLException {
        return conn.createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        return conn.createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return conn.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return conn.createSQLXML();
    }

    @Override
    public boolean isValid(final int i) throws SQLException {
        return conn.isValid(i);
    }

    @Override
    public void setClientInfo(final String string, final String string1) throws SQLClientInfoException {
        conn.setClientInfo(string, string1);
    }

    @Override
    public void setClientInfo(final Properties prprts) throws SQLClientInfoException {
        conn.setClientInfo(prprts);
    }

    @Override
    public String getClientInfo(final String string) throws SQLException {
        return conn.getClientInfo(string);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return conn.getClientInfo();
    }

    @Override
    public Array createArrayOf(final String string, final Object[] os) throws SQLException {
        return conn.createArrayOf(string, os);
    }

    @Override
    public Struct createStruct(final String string, final Object[] os) throws SQLException {
        return conn.createStruct(string, os);
    }

    @Override
    public <T> T unwrap(final Class<T> type) throws SQLException {
        return conn.unwrap(type);
    }

    @Override
    public boolean isWrapperFor(final Class<?> type) throws SQLException {
        return conn.isWrapperFor(type);
    }

    public void setSchema(final String schema) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getSchema() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void abort(final Executor executor) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setNetworkTimeout(final Executor executor, final int milliseconds) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getNetworkTimeout() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
