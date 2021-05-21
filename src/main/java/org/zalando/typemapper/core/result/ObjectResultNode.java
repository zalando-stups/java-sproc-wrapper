package org.zalando.typemapper.core.result;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zalando.typemapper.core.db.DbType;
import org.zalando.typemapper.core.db.DbTypeField;
import org.zalando.typemapper.core.db.DbTypeRegister;
import org.zalando.typemapper.parser.exception.RowParserException;
import org.zalando.typemapper.parser.postgres.ParseUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectResultNode implements DbResultNode {

    private static final Logger LOG = LoggerFactory.getLogger(ObjectResultNode.class);

    private final String name;
    private final String type;
    private final int typeId;
    private List<DbResultNode> children;
    private final Map<String, DbResultNode> nodeMap = new HashMap<>();

    public ObjectResultNode(final String value, final String name, final String typeName, final int typeId,
                            final Connection connection) throws SQLException {
        super();
        this.type = typeName;
        this.typeId = typeId;
        this.children = new ArrayList<>();
        this.name = name;

        List<String> values;
        if (value == null) {
            children = null;
            return;
        }

        try {
            values = ParseUtils.postgresROW2StringList(value);
        } catch (final RowParserException e) {
            throw new SQLException(e);
        }

        final DbType dbType = DbTypeRegister.getDbType(typeId, connection);
        int i = 1;
        for (final String fieldValue : values) {
            if (dbType == null) {
                final String error = "dbType is null for typename: " + typeName;
                LOG.error(error);
                throw new NullPointerException(error);
            }

            final DbTypeField fieldDef = dbType.getFieldByPos(i);
            DbResultNode node;
            if (fieldDef == null) {
                LOG.error("Could not find field in {} for pos {}", dbType, i);
                continue;
            }

            if (fieldDef.getType().equals("USER-DEFINED")) {
                if (fieldDef.getTypeName().equals("hstore")) {
                    node = new SimpleResultNode(fieldValue, fieldDef.getName());
                } else if (fieldDef.getTypeName().equals("enumeration")) {
                    node = new SimpleResultNode(fieldValue, fieldDef.getName());
                } else {
                    node = new ObjectResultNode(fieldValue, fieldDef.getName(), fieldDef.getTypeName(),
                            fieldDef.getTypeId(), connection);
                }
            } else if (fieldDef.getType().equals("ARRAY")) {
                node = new ArrayResultNode(fieldDef.getName(), fieldValue, fieldDef.getTypeName().substring(1),
                        fieldDef.getTypeId(), connection);
            } else if (fieldDef.getType().equals("enum")) {
                /*
                    This is a special case when JDBC driver returns enum as an object.
                    This happens when the enum class is not in the search path. Otherwise it will be handled as a
                    regular field by org.zalando.typemapper.core.fieldMapper.EnumerationFieldMapper
                 */
                node = new SimpleResultNode(fieldValue, this.type);
            } else {
                node = new SimpleResultNode(fieldValue, fieldDef.getName());
            }

            this.children.add(node);
            i++;
        }
    }

    public String getType() {
        return type;
    }

    @Override
    public DbResultNodeType getNodeType() {
        return DbResultNodeType.OBJECT;
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public List<DbResultNode> getChildren() {
        return children;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public DbResultNode getChildByName(final String name) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Lookup the property value of name {} in the object of the name: {} and type: {}", name,
                    this.name,
                    this.type);
        }
        DbResultNode resultNode = nodeMap.get(name);
        if (resultNode == null) {
            for (final DbResultNode node : getChildren()) {
                if (node.getName().equals(name)) {
                    resultNode = node;
                    nodeMap.put(name, resultNode);
                    break;
                }
            }
        }

        return resultNode;
    }

    @Override
    public String toString() {
        return "ObjectResultNode [name=" + name + ", type=" + type + ", typeId=" + typeId + ", children=" + children
               + "]";
    }

}
