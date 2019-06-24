package org.zalando.typemapper.core.result;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MapResultNode implements DbResultNode {

    protected final Map<String, String> value;
    protected final String name;

    public MapResultNode(final Map<String, String> obj, final String name) {
        this.value = obj;
        this.name = name;
    }

    @Override
    public DbResultNodeType getNodeType() {
        return DbResultNodeType.MAP;
    }

    @Override
    public List<DbResultNode> getChildren() {
        return Collections.emptyList();
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
    public String getValue() {
        throw new UnsupportedOperationException();
    }

    public Map<String, String> getMap() {
        return value;
    }

    @Override
    public String toString() {
        return "MapResultNode [value=" + value + ", name=" + name + "]";
    }

}
