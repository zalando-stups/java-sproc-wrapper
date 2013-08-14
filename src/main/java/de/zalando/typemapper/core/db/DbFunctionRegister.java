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
            statement = connection.prepareStatement(
                    "SELECT specific_schema, specific_name,  parameter_name, ordinal_position, data_type, udt_name FROM information_schema.parameters WHERE parameter_mode='OUT';");
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int i = 1;
                String functionSchema = resultSet.getString(i++);
                String functionName = resultSet.getString(i++);
                int sep = functionName.lastIndexOf('_');
                if (sep != -1) {
                    functionName = functionName.substring(0, sep);
                }

                String paramName = resultSet.getString(i++);
                int paramPosition = resultSet.getInt(i++);
                String paramType = resultSet.getString(i++);
                String paramTypeName = resultSet.getString(i++);
                addFunctionParam(functionSchema, functionName, paramName, paramPosition, paramType, paramTypeName);
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
            final int paramPosition, final String paramType, final String paramTypeName) {
        final String functionId = getFunctionIdentifier(functionSchema, functionName);
        DbFunction function = functions.get(functionId);
        if (function == null) {
            function = new DbFunction(functionSchema, functionName);
            addFunction(function);
        }

        if (paramName != null) {
            function.addOutParam(new DbTypeField(paramName, paramPosition, paramType, paramTypeName));
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

    public static void reInitRegistry(final Connection connection) throws SQLException {
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
