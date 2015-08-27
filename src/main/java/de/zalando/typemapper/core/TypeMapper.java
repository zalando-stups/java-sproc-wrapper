package de.zalando.typemapper.core;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import java.util.List;
import java.util.Map;

import org.postgresql.jdbc4.Jdbc4Array;
import org.postgresql.jdbc4.Jdbc4ResultSet;

import org.postgresql.util.PGobject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.RowMapper;

import de.zalando.typemapper.core.db.DbFunction;
import de.zalando.typemapper.core.db.DbFunctionRegister;
import de.zalando.typemapper.core.db.DbTypeField;
import de.zalando.typemapper.core.fieldMapper.ArrayFieldMapper;
import de.zalando.typemapper.core.fieldMapper.ObjectFieldMapper;
import de.zalando.typemapper.core.result.ArrayResultNode;
import de.zalando.typemapper.core.result.DbResultNode;
import de.zalando.typemapper.core.result.DbResultNodeType;
import de.zalando.typemapper.core.result.MapResultNode;
import de.zalando.typemapper.core.result.ObjectResultNode;
import de.zalando.typemapper.core.result.ResultTree;
import de.zalando.typemapper.core.result.SimpleResultNode;
import de.zalando.typemapper.parser.exception.RowParserException;
import de.zalando.typemapper.parser.postgres.ParseUtils;

public class TypeMapper<ITEM> implements RowMapper<ITEM> {

    private static final Logger LOG = LoggerFactory.getLogger(TypeMapper.class);

    private final Class<ITEM> resultClass;
    private final List<Mapping> mappings;

    TypeMapper(final Class<ITEM> resultClass) {
        this.resultClass = resultClass;
        mappings = Mapping.getMappingsForClass(this.resultClass);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public ITEM mapRow(final ResultSet set, final int count) throws SQLException {
        ITEM result = null;
        try {

            Class<ITEM> resultClass = getResultClass();

            if (resultClass.isEnum()) { // Since Enums can't be instantiated, they need to be processed differently
                LOG.debug("{} is an Enum", resultClass.getName());

                String enumValue = set.getString(1);

                if (enumValue != null) {
                    result = (ITEM) Enum.valueOf((Class<? extends Enum>) resultClass, set.getString(1));
                }
            } else {
                final ResultTree resultTree = extractResultTree(set);
                result = resultClass.newInstance();
                fillObject(result, resultTree);
            }
        } catch (final InstantiationException | IllegalAccessException e) {
            throw new SQLException(getResultClass() + " has no public nullary constructor: ", e);
        }

        return result;
    }

    private ResultTree extractResultTree(final ResultSet set) throws SQLException {
        LOG.trace("Extracting result tree");

        // cast to obtain more information from the result set.
        final Jdbc4ResultSet pgSet = set.unwrap(Jdbc4ResultSet.class);
        final ResultSetMetaData rsMetaData = pgSet.getMetaData();

        final ResultTree tree = new ResultTree();

        for (int i = 1; i <= rsMetaData.getColumnCount(); i++) {
            final int typeId = pgSet.getColumnOID(i);
            DbResultNode node = null;

            final Object obj = pgSet.getObject(i);
            final String name = rsMetaData.getColumnName(i);

            // TODO pribeiro We should use polymorphism here. Build like a chain
            if ((obj instanceof PGobject) && ((PGobject) obj).getType().equals("record")) {
                final PGobject pgObj = (PGobject) obj;
                final DbFunction function = DbFunctionRegister.getFunction(name, pgSet.getStatement().getConnection());
                List<String> fieldValues;
                try {
                    fieldValues = ParseUtils.postgresROW2StringList(pgObj.getValue());
                } catch (final RowParserException e) {
                    throw new SQLException(e);
                }

                int j = 1;
                for (final String fieldValue : fieldValues) {
                    final DbTypeField fieldDef = function.getFieldByPos(j);
                    DbResultNode currentNode = null;
                    if (fieldDef.getType().equals("USER-DEFINED")) {
                        currentNode = new ObjectResultNode(fieldValue, fieldDef.getName(), fieldDef.getTypeName(),
                                fieldDef.getTypeId(), pgSet.getStatement().getConnection());
                    } else if (fieldDef.getType().equals("ARRAY")) {
                        currentNode = new ArrayResultNode(fieldDef.getName(), fieldValue,
                                fieldDef.getTypeName().substring(1), fieldDef.getTypeId(),
                                pgSet.getStatement().getConnection());
                    } else {
                        currentNode = new SimpleResultNode(fieldValue, fieldDef.getName());
                    }

                    tree.addChild(currentNode);
                    j++;
                }

                i++;
                continue;
            } else if (obj instanceof Map) {
                node = new MapResultNode((Map<String, String>) obj, name);
            } else if (obj instanceof PGobject) {
                final PGobject pgObj = (PGobject) obj;
                node = new ObjectResultNode(pgObj.getValue(), name, pgObj.getType(), typeId,
                        pgSet.getStatement().getConnection());
            } else if (obj instanceof Jdbc4Array) {
                final Jdbc4Array arrayObj = (Jdbc4Array) obj;

                // TODO pribeiro jdbc driver lacks support for arrays of user defined types. We should whether
                // implement the missing feature in driver or use the current approach (parse string).
                final String typeName = arrayObj.getBaseTypeName();
                final String value = arrayObj.toString();
                node = new ArrayResultNode(name, value, typeName, typeId, pgSet.getStatement().getConnection());
            } else {
                node = new SimpleResultNode(obj, name);
            }

            tree.addChild(node);
        }

        LOG.trace("Extracted ResultTree: {}", tree);

        return tree;
    }

    private void fillObject(final Object result, final ResultTree tree) throws SQLException {
        for (final Mapping mapping : getMappings()) {
            try {
                final DbResultNode node = tree.getChildByName(mapping.getName());

                // TODO pribeiro we need to distinguish between null value and a mapping not defined in the tree
                if (node == null) {

                    if (mapping.isOptionalField()) {
                        mapping.map(result, null);
                    }

                    continue;
                }

                // TODO pribeiro we should use polymorphism instead. Build like a chain.
                if (DbResultNodeType.SIMPLE == node.getNodeType()) {
                    final String fieldStringValue = node.getValue();
                    final Object value = mapping.getFieldMapper().mapField(fieldStringValue, mapping.getFieldClass());

                    mapping.map(result, value);
                } else if (DbResultNodeType.MAP == node.getNodeType()) {

                    // TODO all fields are being converted to String and reverted later. The API forces this approach
                    // (DbResultNode.getValue). This should be improved because it's just causing overhead. The driver
                    // can convert at least the basic types so we should reuse this logic. Result tree should be
                    // improved.
                    // Refactor the if/else statements to a more object-based or polymorphic solution.

                    final Object value = ((MapResultNode) node).getMap();
                    mapping.map(result, value);
                } else if (DbResultNodeType.OBJECT == node.getNodeType()) {
                    final Object value = ObjectFieldMapper.mapFromDbObjectNode(mapping.getFieldClass(),
                            (ObjectResultNode) node, mapping);

                    mapping.map(result, value);
                } else if (DbResultNodeType.ARRAY == node.getNodeType()) {
                    final Object value = ArrayFieldMapper.mapField(mapping.getField(), (ArrayResultNode) node);

                    mapping.map(result, value);
                }
            } catch (final Exception e) {
                LOG.error("Could not map property {} of class {}",
                    new Object[] {mapping.getName(), resultClass.getSimpleName(), e});
            }
        }
    }

    public Class<ITEM> getResultClass() {
        return resultClass;
    }

    public List<Mapping> getMappings() {
        return mappings;
    }

}
