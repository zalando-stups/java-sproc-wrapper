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

    // optimize for concurrent reads, since we should have a small amount of writes.
    // Use the copy on write pattern concurrency pattern.
    // Use volatile variable to guaranty that any thread that reads the field will see the most recently written value
    private static volatile Map<String, DbTypeRegister> registers = ImmutableMap.of();

    private static final Object typeIdToFQNLock = new Object();
    private static final Object typeRegisterLock = new Object();

    private final Map<String, DbType> typeByName;
    private final List<String> searchPath;

    private final Map<String, String> typeFQN;

    private final Map<Integer, String> typeIdToFQN;

    public DbTypeRegister(final Connection connection) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            searchPath = getSearchPath(connection);
            typeByName = new HashMap<String, DbType>();

            typeIdToFQN = new ConcurrentHashMap<>();

            final HashMap<String, List<String>> typeNameToFQN = new HashMap<String, List<String>>();

            //J-
//            statement = connection.prepareStatement(
//                    "select tn.nspname as type_schema, " +
//                    "t.typname as type_name, " +
//                    "t.oid as type_id, " +
//                    "t.typtype as type_type, " +
//                    "a.attname as att_name, " +
//                    "(CASE WHEN ((t2.typelem <> (0)::oid) AND (t2.typlen = -1)) " +
//                    "THEN 'ARRAY'::text  WHEN (tn2.nspname = 'pg_catalog'::name) " +
//                    "THEN format_type(a.atttypid, NULL::integer) " +
//                    "ELSE 'USER-DEFINED'::text END) as att_type, " +
//                    "t2.typname, " +
//                    "t2.oid, " +
//                    "a.attnum as att_position, " +
//                    "t.typelem <> 0 AND t.typlen = (-1) as is_array, " +
//                    "t.typelem " +
//                    "from pg_type as t " +
//                    "join pg_namespace as tn on t.typnamespace = tn.oid " +
//                    "left join pg_class c on t.typrelid = c.oid "+
//                    "left join pg_attribute as a on a.attrelid = t.typrelid and a.attnum > 0 and not a.attisdropped " +
//                    "left join pg_type as t2 on a.atttypid = t2.oid " +
//                    "left join pg_namespace as tn2 on t2.typnamespace = tn2.oid " +
//                    "where (t.typtype = 'e' OR (t.typtype = 'c' AND c.relkind = 'c'::char) OR (t.typelem <> 0 AND  t.typlen = (-1) AND t.typtype = 'b'))" +
//                    "and tn.nspname not in ( 'pg_catalog', 'pg_toast', 'information_schema' ) " +
//                    "and t.typowner > 0 " +
//                    "order by is_array, t.typowner, type_schema, type_name, att_position");
            // New by kuchkin [27.10.2015]
            statement = connection.prepareStatement(
                    "select tn.nspname as type_schema, "
                            + "  t.typname as type_name, "
                            + "  t.oid as type_id, "
                            + "  t.typtype as type_type, "
                            + "  a.attname as att_name, "
                            + "  (CASE WHEN ((t2.typelem <> (0)::oid) AND (t2.typlen = -1)) THEN 'ARRAY'::text "
                            + "        WHEN (tn2.nspname = 'pg_catalog'::name) THEN format_type(a.atttypid, NULL::integer) ELSE 'USER-DEFINED'::text "
                            + "  END) as att_type, "
                            + "  t2.typname, "
                            + "  t2.oid, "
                            + "  a.attnum as att_position, "
                            + "  t.typelem <> 0 AND t.typlen = (-1) as is_array, "
                            + "  t.typelem "
                            + "from pg_type as t "
                            + "join pg_namespace as tn "
                            + "  on t.typnamespace = tn.oid "
                            + "left join pg_class c "
                            + "  on t.typrelid = c.oid "
                            + "left join pg_attribute as a "
                            + "  on a.attrelid = t.typrelid "
                            + "  and a.attnum > 0 "
                            + "  and not a.attisdropped "
                            + "left join pg_type as t2 "
                            + "  on a.atttypid = t2.oid "
                            + "left join pg_namespace as tn2 "
                            + "  on t2.typnamespace = tn2.oid "
                            + "left join (select row_number() over () as n, * from unnest(current_schemas(false))) sl (n, schema_name) "
                            + "  on sl.schema_name = tn.nspname "
                            + "where (t.typtype = 'e' OR (t.typtype = 'c' AND c.relkind = 'c'::char) OR (t.typelem <> 0 AND t.typlen = (-1) AND t.typtype = 'b')) "
                            + "  and tn.nspname not in ( 'pg_catalog', 'pg_toast', 'information_schema' ) "
                            + "  and t.typowner > 0 "
                            + "order by sl.n, is_array, t.typowner, type_schema, type_name, att_position"
            );
            //J+
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int i = 1;
                final String typeSchema = resultSet.getString(i++);
                final String typeName = resultSet.getString(i++);
                final int typeId = resultSet.getInt(i++);
                final String typeType = resultSet.getString(i++);
                final String fieldName = resultSet.getString(i++);
                final String fieldType = resultSet.getString(i++);
                final String fieldTypeName = resultSet.getString(i++);
                final int fieldTypeId = resultSet.getInt(i++);
                final int fieldPosition = resultSet.getInt(i++);
                final boolean isArray = resultSet.getBoolean(i++);
                final int typeElem = resultSet.getInt(i++);

                addField(typeSchema, typeName, typeId, fieldName, fieldPosition, fieldType, fieldTypeName, fieldTypeId,
                    typeType, isArray, typeNameToFQN, typeElem);
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
        return typeByName;
    }

    private void addField(final String typeSchema, final String typeName, final int typeId, final String fieldName,
            final int fieldPosition, final String fieldType, final String fieldTypeName, final int fieldTypeId,
            final String typeType, final boolean isArray, final Map<String, List<String>> typeNameToFQN,
            final int typeElem) {

        if (isArray) {

            // if it's an array, we should resolve the element type
            String typeFQN = typeIdToFQN.get(typeId);
            if (typeFQN == null) {

                // non arrays are processed first
                typeFQN = typeIdToFQN.get(typeElem);
                if (typeFQN != null) {
                    typeIdToFQN.put(typeId, typeFQN);
                }
            }
        } else {
            final String typeFQN = getTypeIdentifier(typeSchema, typeName);
            DbType type = typeByName.get(typeFQN);
            if (type == null) {
                type = new DbType(typeSchema, typeName, typeId);
                addType(type, typeNameToFQN);
            }

            // do we have an enum?
            if ("e".equals(typeType)) {
                type.addField(new DbTypeField(typeName, 1, "enum", "enum", fieldTypeId));
            } else {
                if (null != fieldName) {
                    type.addField(new DbTypeField(fieldName, fieldPosition, fieldType, fieldTypeName, fieldTypeId));
                } else {
                    LOG.warn("{}.{} has no attributes!", typeSchema, typeName);
                }
            }
        }
    }

    private void addType(final DbType type, final Map<String, List<String>> typeNameToFQN) {
        final String id = getTypeIdentifier(type.getSchema(), type.getName());
        typeByName.put(id, type);
        typeIdToFQN.put(type.getId(), id);

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

            // This should be improved. If the sproc specifies the schema of one type, we might end up using the wrong
            // type (with the same name and different schema) because we "resolve" the conflict using the search path.
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

    public static String getTypeIdentifier(final String typeSchema, final String typeName) {
        return typeSchema + "." + typeName;
    }

    public static DbType getDbType(final String name, final Connection connection) throws SQLException {
        final DbTypeRegister register = getRegistry(connection);

        // fqName concept is wrong. we should know not only the name, but the schema as well. This should be reworked.
        final String fqName = register.typeFQN.get(name);

        return fqName == null ? null : register.typeByName.get(fqName);
    }

    public static DbType getDbType(final int id, final Connection connection) throws SQLException {
        final DbTypeRegister register = getRegistry(connection);
        DbType type = null;

        String typeFQN = register.typeIdToFQN.get(id);
        if (typeFQN == null) {
            boolean load = false;

            // only query once
            synchronized (typeIdToFQNLock) {
                typeFQN = register.typeIdToFQN.get(id);
                if (typeFQN == null) {
                    load = true;

                    // try to load
                    final String sql =                                                   //
                        "          SELECT tn.nspname AS type_schema ,"                   //
                            + "           (CASE"                                         //
                            + "                WHEN t2.typname IS NULL"                  //
                            + "                THEN t.typname"                           //
                            + "                ELSE t2.typname"                          //
                            + "            END) AS type_name"                            //
                            + "      FROM pg_type AS t"                                  //
                            + "      JOIN pg_namespace AS tn ON t.typnamespace = tn.oid" //
                            + " LEFT JOIN pg_type AS t2 ON t.typelem <> 0 AND t.typlen = (-1) AND t.typelem = t2.oid"
                            + "     WHERE t.oid = ?";

                    ResultSet res = null;
                    PreparedStatement ps = null;
                    try {
                        ps = connection.prepareStatement(sql);
                        ps.setInt(1, id);
                        res = ps.executeQuery();
                        if (res.next()) {
                            typeFQN = getTypeIdentifier(res.getString(1), res.getString(2));
                        }

                    } finally {
                        if (res != null) {
                            res.close();
                        }

                        if (ps != null) {
                            ps.close();
                        }
                    }

                    if (typeFQN != null) {
                        register.typeIdToFQN.put(id, typeFQN);
                    }
                }
            }

            if (load) {
                LOG.info("Type with oid {} not found in local cache.", id);
            }
        }

        if (typeFQN != null) {
            type = register.typeByName.get(typeFQN);
        }

        return type;
    }

    // copy on write design pattern. Slow on writes, but really fast on reads. This pattern fits our
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
            synchronized (typeRegisterLock) {
                cachedRegisters = registers.get(connectionURL);

                // Second check (with locking)
                if (cachedRegisters == null) {
                    cachedRegisters = new DbTypeRegister(connection);

                    // read optimization. We are not expecting so many different DBMS URLs, so read is really fast and
                    // the write is a little bit slower which is ok because most of the
                    // times we will use what we have in memory.  Copy all previous entries to the new immutable map.
                    // This soudnd't happen very often.
                    registers = ImmutableMap.<String, DbTypeRegister>builder().putAll(registers)
                                            .put(connectionURL, cachedRegisters).build();
                }
            }
        }

        return cachedRegisters;
    }

    public static void reInitRegister(final Connection connection) throws SQLException {
        registers = ImmutableMap.of(connection.getMetaData().getURL(), new DbTypeRegister(connection));
    }
}
