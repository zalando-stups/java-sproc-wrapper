package de.zalando.typemapper.core.result;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import de.zalando.typemapper.core.db.DbType;
import de.zalando.typemapper.core.db.DbTypeRegister;
import de.zalando.typemapper.parser.exception.ArrayParserException;
import de.zalando.typemapper.parser.postgres.ParseUtils;

public class ArrayResultNode implements DbResultNode {

    private String name;
    private String type;
    private List<DbResultNode> children;
    private DbType typeDef;

    public ArrayResultNode(final String name, final String value, final String typeName, final Connection connection)
        throws SQLException {
        this.name = name;
        this.type = typeName;
        this.typeDef = DbTypeRegister.getDbType(typeName, connection);
        this.children = new ArrayList<DbResultNode>();

        List<String> elements;
        if (value != null) {
            try {
                elements = ParseUtils.postgresArray2StringList(value);
            } catch (ArrayParserException e) {
                throw new SQLException("Failed to parse array " + name + " of type " + typeName, e);
            }

            for (String element : elements) {
                if (typeDef != null) {
                    children.add(new ObjectResultNode(element, "", typeName, connection));
                } else {
                    children.add(new SimpleResultNode(element, ""));
                }
            }
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public DbResultNodeType getNodeType() {
        return DbResultNodeType.ARRAY;
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
    public DbResultNode getChildByName(final String name) {
        return null;
    }

    @Override
    public String toString() {
        return "ArrayResultNode [name=" + name + ", type=" + type + ", children=" + children + ", typeDef=" + typeDef
                + "]";
    }

}
