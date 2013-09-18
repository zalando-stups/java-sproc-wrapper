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
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

public class DbTypeRegister {

    private static final Logger LOG = LoggerFactory.getLogger(DbTypeRegister.class);

    // optimize for concurrent reads since we should have a small number of writes.
    // Use copy on write concurrency pattern.
    // Use volatile variable to guaranty that any thread that reads the field will see the most recently written value
    private static volatile Map<String, DbTypeRegister> registers = ImmutableMap.of();

    private static Map<String, DbType> dbTypeCache = new ConcurrentHashMap<>();

    private final Map<String, DbType> types;
    private final Map<String, String> typeFQN;
    private final List<String> searchPath;

    public DbTypeRegister(final Connection connection) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            searchPath = getSearchPath(connection);
            types = new HashMap<String, DbType>();

            final HashMap<String, List<String>> typeNameToFQN = new HashMap<String, List<String>>();

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

                addField(typeSchema, typeName, fieldName, fieldPosition, fieldType, fieldTypeName, typeType, isArray,
                    typeNameToFQN);
            }

            typeFQN = buildTypeFQN(typeNameToFQN);
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
            final boolean isArray, final Map<String, List<String>> typeNameToFQN) {
        final String typeId = getTypeIdentifier(typeSchema, typeName);
        DbType type = types.get(typeId);
        if (type == null) {
            type = new DbType(typeSchema, typeName);
            addType(type, typeNameToFQN);
        }

        // do we have an enum?
        if ("e".equals(typeType)) {
            type.addField(new DbTypeField(typeName, 1, "enum", "enum"));
        } else {
            if (null != fieldName) {
                type.addField(new DbTypeField(fieldName, fieldPosition, fieldType, fieldTypeName));
            } else {
                LOG.warn("{}.{} has no attributes!", typeSchema, typeName);
            }
        }
    }

    private void addType(final DbType type, final Map<String, List<String>> typeNameToFQN) {
        final String id = getTypeIdentifier(type.getSchema(), type.getName());
        types.put(id, type);

        List<String> list = typeNameToFQN.get(type.getName());
        if (list == null) {
            list = new LinkedList<String>();
            typeNameToFQN.put(type.getName(), list);
        }

        list.add(id);
    }

    private Map<String, String> buildTypeFQN(final Map<String, List<String>> typeNameToFQN) {
        final ImmutableMap.Builder<String, String> result = ImmutableMap.builder();

        for (final Entry<String, List<String>> entry : typeNameToFQN.entrySet()) {
            final List<String> types = entry.getValue();

            // This should be improved. If the sproc specifies the schema of one type, we might end up using the wrong type (with the same name
            // and different schema) because we "resolve" the conflict using the search path.
            // Main problems:
            // 1 - One might hard code the schema on the sproc and SP might use the wrong type
            // 2 - We might have the same jdbc URL with different search paths
            final String fqn = types.size() == 1 ? types.get(0) : SearchPathSchemaFilter.filter(types, searchPath);
            if (fqn != null) {

                // if it's null, we shouldn't put it on the map
                result.put(entry.getKey(), fqn);
            }
        }

        return result.build();
    }

    private String getTypeIdentifier(final String typeSchema, final String typeName) {
        return typeSchema + "." + typeName;
    }

    public static DbType getDbType(final String name, final Connection connection) throws SQLException {
        DbTypeRegister register = getRegistry(connection);

        // fqName concept is wrong. we should know not only the name, but the schema as well. This should be reworked.
        final String fqName = register.typeFQN.get(name);

        return fqName == null ? null : register.types.get(fqName);
    }

    // copy on write design pattern. Slow on writes, but fast on reads. This pattern fits our
    // situation because we are not expecting so many different jdbc urls.
    public static DbTypeRegister getRegistry(final Connection connection) throws SQLException {

        // if connection URL is null we can't proceed. fail fast
        Preconditions.checkNotNull(connection);

        final String connectionURL = connection.getMetaData().getURL();
        Preconditions.checkNotNull(connection.getMetaData().getURL(), "connection URL is null");

        // check if we have the value in memory
        DbTypeRegister cachedRegisters = registers.get(connectionURL);

        // First check (no locking)
        if (cachedRegisters == null) {
            synchronized (DbTypeRegister.class) {
                cachedRegisters = registers.get(connectionURL);

                // Second check (with locking)
                if (cachedRegisters == null) {
                    cachedRegisters = new DbTypeRegister(connection);

                    // read optimization. We are not expecting so many different JDBC URLs, so read is really fast and
                    // write is a little bit slower which is completely fine because most of the
                    // times we will use what we have in memory. Copy all previous entries to the new immutable map.
                    registers = ImmutableMap.<String, DbTypeRegister>builder().putAll(registers)
                                            .put(connectionURL, cachedRegisters).build();
                }
            }
        }

        return cachedRegisters;
    }

    public static void reInitRegister(final Connection connection) throws SQLException {
        dbTypeCache.clear();
        registers = ImmutableMap.of(connection.getMetaData().getURL(), new DbTypeRegister(connection));
    }
}
