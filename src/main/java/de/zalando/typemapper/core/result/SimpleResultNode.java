package de.zalando.typemapper.core.result;

import java.util.ArrayList;
import java.util.List;

public class SimpleResultNode implements DbResultNode {

    protected final String value;
    protected final String name;

    public SimpleResultNode(final Object obj, final String name) {
        if (obj == null) {
            this.value = null;
        } else {
            this.value = obj.toString();
        }

        this.name = name;
    }

    @Override
    public DbResultNodeType getNodeType() {
        return DbResultNodeType.SIMPLE;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public List<DbResultNode> getChildren() {
        return new ArrayList<DbResultNode>();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public DbResultNode getChildByName(final String name) {
        return null;
    }

    @Override
    public String toString() {
        return "SimpleResultNode [value=" + value + ", name=" + name + "]";
    }

}
