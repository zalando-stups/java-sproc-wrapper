package de.zalando.sprocwrapper.proxy;

import java.lang.reflect.Method;

import java.sql.Connection;
import java.sql.SQLException;

import org.postgresql.util.PGobject;

import com.typemapper.postgres.PgTypeHelper;

/**
 * @author  jmussler
 */
class OtherStoredProcedureParameter extends StoredProcedureParameter {

    public OtherStoredProcedureParameter(final Class<?> clazz, final Method m, final String typeName, final int sqlType,
            final int javaPosition, final boolean sensitive) {
        super(clazz, m, typeName, sqlType, javaPosition, sensitive);
    }

    @Override
    public Object mapParam(final Object value, final Connection connection) {
        if (value == null) {
            return null;
        }

        Object result = value;
        if (clazz.isEnum()) {

            // HACK: should be implemented in PgTypeHelper
            final PGobject pgobj = new PGobject();
            pgobj.setType(typeName);
            try {
                pgobj.setValue(((Enum<?>) value).name());
            } catch (final SQLException ex) {
                if (sensitive) {
                    LOG.error("Failed to set PG object value (sensitive parameter, stacktrace hidden)");
                } else {
                    LOG.error("Failed to set PG object value", ex);
                }
            }

            result = pgobj;
        } else {
            try {
                result = PgTypeHelper.asPGobject(value, typeName, connection);
            } catch (final SQLException ex) {
                if (sensitive) {
                    LOG.error("Failed to serialize PG object (sensitive parameter, stacktrace hidden)");
                } else {
                    LOG.error("Failed to serialize PG object", ex);
                }
            }
        }

        return result;
    }

}
