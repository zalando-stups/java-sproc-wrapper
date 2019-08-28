package de.zalando.typemapper.core.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DbFunctionRegister {

    private static final String FUNCTION_DETAILS = "SELECT ss.n_nspname AS specific_schema, "                                 //
            + "       ss.proname::text AS specific_name, "                                                                    //
            + "       (ss.x).n AS ordinal_position, "                                                                         //
            + "       NULLIF(ss.proargnames[(ss.x).n], ''::text) AS parameter_name, "                                         //
            + "       CASE "                                                                                                  //
            + "           WHEN t.typelem <> (0)::oid AND t.typlen = (-1) "                                                    //
            + "           THEN 'ARRAY'::text "                                                                                //
            + "           WHEN nt.nspname = 'pg_catalog'::name "                                                              //
            + "           THEN format_type(t.oid, NULL::INTEGER) "                                                            //
            + "           ELSE 'USER-DEFINED'::text "                                                                         //
            + "       END AS formatted_type_name, "                                                                           //
            + "       ss.p_oid AS procedure_oid, "                                                                            //
            + "       t.typname AS unformatted_type_name, "                                                                   //
            + "       t.oid AS type_oid "                                                                                     //
            + "  FROM pg_type t, "                                                                                            //
            + "       pg_namespace nt, "                                                                                      //
            + "       ( "                                                                                                     //
            + "         SELECT n.nspname AS n_nspname, "                                                                      //
            + "                p.proname, "                                                                                   //
            + "                p.oid AS p_oid, "                                                                              //
            + "                p.proargnames, "                                                                               //
            + "                p.proargmodes, "                                                                               //
            + "                information_schema._pg_expandarray(COALESCE(p.proallargtypes, (p.proargtypes)::oid[])) AS x "  //
            + "           FROM pg_namespace n, "                                                                              //
            + "                pg_proc p "                                                                                    //
            + "          WHERE ((n.oid = p.pronamespace) "                                                                    //
            + "            AND (pg_has_role(p.proowner, 'USAGE'::text) OR  has_function_privilege(p.oid, 'EXECUTE'::text))) " //
            + "        ) ss "                                                                                                 //
            + "WHERE t.oid = (ss.x).x "                                                                                       //
            + "  AND t.typnamespace = nt.oid "                                                                                //
            + "  AND ss.proargmodes[(ss.x).n] = ANY ('{o,b,t}'::char[]);";

    private Map<String, DbFunction> functions = null;
    private Map<String, List<String>> functionNameToFQName = null;
    private static Map<String, DbFunctionRegister> registers;
    private List<String> searchPath = null;

    public DbFunctionRegister(final Connection connection) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            searchPath = DbTypeRegister.getSearchPath(connection);
            functionNameToFQName = new HashMap<String, List<String>>();
            functions = new HashMap<String, DbFunction>();
            statement = connection.prepareStatement(FUNCTION_DETAILS);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int i = 1;
                final String functionSchema = resultSet.getString(i++);
                final String functionName = resultSet.getString(i++);
                final int paramPosition = resultSet.getInt(i++);
                final String paramName = resultSet.getString(i++);
                final String paramType = resultSet.getString(i++);
                final long procedureId = resultSet.getLong(i++);
                final String paramTypeName = resultSet.getString(i++);
                final long paramTypeId = resultSet.getLong(i++);

                addFunctionParam(functionSchema, functionName, paramName, paramPosition, paramType, procedureId,
                    paramTypeName, paramTypeId);
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
                statement.close();
            }
        }
    }

    private void addFunctionParam(final String functionSchema, final String functionName, final String paramName,
                                  final int paramPosition, final String paramType, final long procedureId, final String paramTypeName,
                                  final long paramTypeId) {

        final String functionId = getFunctionIdentifier(functionSchema, functionName);
        DbFunction function = functions.get(functionId);
        if (function == null) {
            function = new DbFunction(functionSchema, functionName);
            addFunction(function);
        }

        if (paramName != null) {
            function.addOutParam(new DbTypeField(paramName, paramPosition, paramType, paramTypeName, paramTypeId));
        }
    }

    private void addFunction(final DbFunction function) {
        String functionIdentifier = getFunctionIdentifier(function.getSchema(), function.getName());
        functions.put(functionIdentifier, function);

        List<String> list = functionNameToFQName.get(function.getName());
        if (list == null) {
            list = new LinkedList<String>();
            functionNameToFQName.put(function.getName(), list);
        }

        list.add(functionIdentifier);
    }

    private static String getFunctionIdentifier(final String schema, final String functionName) {
        return schema + "." + functionName;
    }

    public static final DbFunction getFunction(final String name, final Connection connection) throws SQLException {
        if (registers == null) {
            initRegistry(connection, "default");
        }

        for (DbFunctionRegister register : registers.values()) {
            List<String> list = register.functionNameToFQName.get(name);
            if (list.size() == 1) {
                return register.functions.get(list.get(0));
            } else {
                String fqName = SearchPathSchemaFilter.filter(list, register.searchPath);
                if (fqName != null) {
                    DbFunction function = register.functions.get(fqName);
                    if (function != null) {
                        return function;
                    }
                }
            }
        }

        return null;
    }

    public static synchronized void reInitRegistry(final Connection connection) throws SQLException {
        if (registers == null) {
            registers = new HashMap<String, DbFunctionRegister>();
        }

        registers.put("default", new DbFunctionRegister(connection));
    }

    public static synchronized void initRegistry(final Connection connection, final String name) throws SQLException {
        if (registers == null) {
            registers = new HashMap<String, DbFunctionRegister>();
        }

        if (!registers.containsKey(name)) {
            registers.put(name, new DbFunctionRegister(connection));
        }

    }

}
