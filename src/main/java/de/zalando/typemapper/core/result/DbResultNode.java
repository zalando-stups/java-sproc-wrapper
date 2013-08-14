package de.zalando.typemapper.core.result;

import java.util.List;

public interface DbResultNode {

    String getName();

    DbResultNodeType getNodeType();

    String getValue();

    List<DbResultNode> getChildren();

    DbResultNode getChildByName(String name);

}
