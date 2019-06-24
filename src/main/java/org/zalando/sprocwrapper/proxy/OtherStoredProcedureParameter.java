package org.zalando.sprocwrapper.proxy;

import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zalando.typemapper.postgres.PgTypeHelper;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

/**
 * @author jmussler
 */
class OtherStoredProcedureParameter extends StoredProcedureParameter {
    private static final Logger LOG = LoggerFactory.getLogger(OtherStoredProcedureParameter.class);

    public OtherStoredProcedureParameter(final Class<?> clazz, final Method m, final String typeName, final int sqlType,
                                         final int javaPosition, final boolean sensitive) {
        super(clazz, m, typeName, sqlType, javaPosition, sensitive);
    }

    @Override
    public Object mapParam(final Object value, final Connection connection) {
        if (value == null) {
            if (clazz.isEnum()) {
                /*
                * In situation when there is null value passed as an argument and
                * argument type maps to custom database type which inherited from ENUM
                * sql type "OTHER" will fallback to sql type "VARCHAR" what will cause an exception
                 *  "No function matches the given name and argument types. You might need to add explicit type casts."
                  *  That happens because SP expects custom type but not VARCHAR
                 *
                */
                final PGobject pgobj = new PGobject();
                pgobj.setType(typeName);
                return pgobj;
            }
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
        } else if (clazz.isAssignableFrom(UUID.class)) {
            final PGobject pgobj = new PGobject();
            pgobj.setType(typeName);
            try {
                pgobj.setValue(value.toString());
            } catch (SQLException ex) {
                if (sensitive) {
                    LOG.error("Failed to set PG object value (sensitive parameter, stacktrace hidden)");
                } else {
                    LOG.error("Failed to set PG object value", ex);
                }
            }
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
