package de.zalando.typemapper.core.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.StringUtils;

public class DbTypeRegister {

    private static final Logger LOG = LoggerFactory.getLogger(DbTypeRegister.class);

    private static Map<String, DbTypeRegister> registers = null;
    private static Map<String, DbType> dbTypeCache = new ConcurrentHashMap<>();

    private Map<String, DbType> types = null;
    private Map<String, List<String>> typeNameToFQN = null;
    private List<String> searchPath = null;

    public DbTypeRegister(final Connection connection) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            searchPath = getSearchPath(connection);
            typeNameToFQN = new HashMap<String, List<String>>();
            types = new HashMap<String, DbType>();

            //J-
            statement = connection.prepareStatement(
                    "select tn.nspname as type_schema, " +
                    "t.typname as type_name, " +
                    "t.typtype as type_type, " +
                    "a.attname as att_name, " +
                    "(CASE WHEN ((t2.typelem <> (0)::oid) AND (t2.typlen = -1)) " +
                    "THEN 'ARRAY'::text  WHEN (tn2.nspname = 'pg_catalog'::name) " +
                    "THEN format_type(a.atttypid, NULL::integer) " +
                    "ELSE 'USER-DEFINED'::text END) as att_type, " +
                    "t2.typname, " +
                    "a.attnum as att_position, " +
                    "t.typarray > 0 as is_array " +
                    "from pg_type as t " +
                    "join pg_namespace as tn on t.typnamespace = tn.oid " +
                    "left join pg_class c on t.typrelid = c.oid "+
                    "left join pg_attribute as a on a.attrelid = t.typrelid and a.attnum > 0 and not a.attisdropped " +
                    "left join pg_type as t2 on a.atttypid = t2.oid " +
                    "left join pg_namespace as tn2 on t2.typnamespace = tn2.oid " +
                    "where (t.typtype = 'e' OR (t.typtype = 'c' AND c.relkind = 'c'::char))" +
                    "and tn.nspname not in ( 'pg_catalog', 'pg_toast', 'information_schema' ) " +
                    "and t.typowner > 0 " +
                    "order by t.typowner, type_schema, type_name, att_position");
            //J+
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int i = 1;
                final String typeSchema = resultSet.getString(i++);
                final String typeName = resultSet.getString(i++);
                final String typeType = resultSet.getString(i++);
                final String fieldName = resultSet.getString(i++);
                final String fieldType = resultSet.getString(i++);
                final String fieldTypeName = resultSet.getString(i++);
                final int fieldPosition = resultSet.getInt(i++);
                final boolean isArray = resultSet.getBoolean(i++);

                addField(typeSchema, typeName, fieldName, fieldPosition, fieldType, fieldTypeName, typeType, isArray);
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

    public static List<String> getSearchPath(final Connection connection) throws SQLException {
        final ResultSet searchPathResult = connection.createStatement().executeQuery("show search_path;");
        searchPathResult.next();

        final String searchPathStr = searchPathResult.getString(1);
        return Arrays.asList(searchPathStr.split("\\s*,\\s*"));
    }

    public List<String> getSearchPath() {
        return searchPath;
    }

    public Map<String, DbType> getTypes() {
        return types;
    }

    private void addField(final String typeSchema, final String typeName, final String fieldName,
            final int fieldPosition, final String fieldType, final String fieldTypeName, final String typeType,
            final boolean isArray) {
        final String typeId = getTypeIdentifier(typeSchema, typeName);
        DbType type = types.get(typeId);
        if (type == null) {
            type = new DbType(typeSchema, typeName);
            addType(type);

        }

        // do we have an enum?
        if ("e".equals(typeType)) {
            type.addField(new DbTypeField(typeName, 1, "enum", "enum"));
        } else {
            type.addField(new DbTypeField(fieldName, fieldPosition, fieldType, fieldTypeName));
        }
    }

    private void addType(final DbType type) {
        final String id = getTypeIdentifier(type.getSchema(), type.getName());
        types.put(id, type);

        List<String> list = typeNameToFQN.get(type.getName());
        if (list == null) {
            list = new LinkedList<String>();
            typeNameToFQN.put(type.getName(), list);
        }

        list.add(id);
    }

    private String getTypeIdentifier(final String typeSchema, final String typeName) {
        return typeSchema + "." + typeName;
    }

    public static DbType getDbType(final String name, final Connection connection) throws SQLException {
        final Map<String, DbTypeRegister> registry = initRegistry("default", connection);
        DbType dbType = dbTypeCache.get(name);
        if (dbType == null) {
            for (final DbTypeRegister register : registry.values()) {
                final List<String> list = register.typeNameToFQN.get(name);
                if (list != null) {
                    if (list.size() == 1) {
                        dbType = register.types.get(list.get(0));
                        dbTypeCache.put(name, dbType);
                        break;
                    } else {
                        final String fqName = SearchPathSchemaFilter.filter(list, register.searchPath);
                        if (fqName != null) {
                            final DbType result = register.types.get(fqName);
                            if (result != null) {
                                dbType = result;
                                dbTypeCache.put(name, dbType);
                                break;
                            }
                        }
                    }
                }
            }
        }

        return dbType;
    }

    public static synchronized Map<String, DbTypeRegister> initRegistry(final String name, final Connection connection)
        throws SQLException {
        if (registers == null) {
            registers = new HashMap<String, DbTypeRegister>();
        }

        if (!registers.containsKey(name)) {
            final DbTypeRegister register = new DbTypeRegister(connection);
            registers.put(name, register);

            final String searchPath = StringUtils.arrayToDelimitedString(register.getSearchPath().toArray(), ", ");
            LOG.info("Initialized type register '{}' with search path '{}' and {} types",
                new Object[] {name, searchPath, register.getTypes().size()});
        }

        return registers;
    }

    public static void reInitRegister(final Connection connection) throws SQLException {
        if (registers == null) {
            registers = new HashMap<String, DbTypeRegister>();
        }

        dbTypeCache.clear();
        registers.put("default", new DbTypeRegister(connection));
    }
}
