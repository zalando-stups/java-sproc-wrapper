package de.zalando.sprocwrapper.proxy.executors;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.zalando.sprocwrapper.SProcCall.AdvisoryLock;
import de.zalando.sprocwrapper.dsprovider.SameConnectionDatasource;

/**
 * This Executor wraps stored procedure calls that use advisory locks and / or need different statement timeouts set.
 *
 * @author  jmussler
 */
public class ExecutorWrapper implements Executor {

    private final Executor executor;
    private final long timeoutInMilliSeconds;
    private final AdvisoryLock lock;

    private static final Logger LOG = LoggerFactory.getLogger(ExecutorWrapper.class);

    public ExecutorWrapper(final Executor e, final long t, final AdvisoryLock a) {
        executor = e;
        timeoutInMilliSeconds = t;
        lock = a;
    }

    private void setTimeout(final Connection conn) throws SQLException {
        if (timeoutInMilliSeconds <= 0) {
            return;
        }

        LOG.debug("Setting statement timeout {}", timeoutInMilliSeconds);

        final Statement st = conn.createStatement();
        st.execute("SET application_name TO 'timeout:" + timeoutInMilliSeconds + "'");
        st.execute("SET statement_timeout TO " + timeoutInMilliSeconds);
        st.close();
    }

    private void resetTimeout(final Connection conn) throws SQLException {
        if (timeoutInMilliSeconds <= 0) {
            return;
        }

        LOG.debug("Resetting statement timeout");

        final Statement st = conn.createStatement();
        st.execute("RESET statement_timeout");
        st.execute("RESET application_name");
        st.close();
    }

    private boolean lockAdvisoryLock(final Connection conn) throws SQLException {
        if (lock == AdvisoryLock.NO_LOCK) {
            return true;
        }

        final Statement st = conn.createStatement();
        final ResultSet rs = st.executeQuery("SELECT pg_advisory_lock(" + lock.getSprocId() + ") AS \"" + lock.name()
                    + "\";");

        boolean b = false;
        if (rs.next()) {
            b = true;
        }

        rs.close();
        st.close();
        return b;
    }

    private boolean unlockAdvisoryLock(final Connection conn) throws SQLException {
        if (lock == AdvisoryLock.NO_LOCK) {
            return true;
        }

        final Statement st = conn.createStatement();
        final ResultSet rs = st.executeQuery("SELECT pg_advisory_unlock(" + lock.getSprocId() + ")");
        boolean b = false;
        if (rs.next()) {
            b = rs.getBoolean(1);
        }

        rs.close();
        st.close();
        return b;
    }

    @Override
    public Object executeSProc(final DataSource ds, final String sql, final Object[] args, final int[] types,
            final Object[] originalArgs, final Class returnType) {

        SameConnectionDatasource sameConnDs = null;

        try {
            sameConnDs = new SameConnectionDatasource(ds.getConnection());

            setTimeout(sameConnDs.getConnection());

            if (!lockAdvisoryLock(sameConnDs.getConnection())) {
                throw new RuntimeException("Could not acquire AdvisoryLock " + lock.name());
            }

            return executor.executeSProc(sameConnDs, sql, args, types, originalArgs, returnType);

        } catch (final SQLException e) {

            throw new RuntimeException("SQL Exception in execute sproc: " + sql, e);

        } finally {
            if (sameConnDs != null) {
                try {

                    if (timeoutInMilliSeconds > 0) {
                        try {
                            resetTimeout(sameConnDs.getConnection());
                        } catch (final SQLException ex) {
                            LOG.error("Exception in reseting statement timeout!", ex);
                        }
                    }

                    // unlock in all cases, locks not owned by this session cannot be unlocked
                    if (lock != AdvisoryLock.NO_LOCK) {
                        try {
                            unlockAdvisoryLock(sameConnDs.getConnection());
                        } catch (final SQLException ex) {
                            LOG.error("Exception in reseting advisory lock!", ex);
                        }
                    }
                } finally {
                    try {
                        sameConnDs.close();
                    } catch (final SQLException ex) {
                        LOG.error("Exception in closing underlying connection!", ex);
                    }
                }
            }
        }
    }
}
