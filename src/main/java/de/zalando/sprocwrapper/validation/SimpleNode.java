package de.zalando.sprocwrapper.validation;

import javax.validation.Path;

public class SimpleNode implements Path.Node {

    private final String name;
    private final boolean inIterable;
    private final Integer index;
    private final Object key;

    public SimpleNode(final String name, final boolean inIterable, final Integer index, final Object key) {
        this.name = name;
        this.inIterable = inIterable;
        this.index = index;
        this.key = key;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isInIterable() {
        return inIterable;
    }

    @Override
    public Integer getIndex() {
        return index;
    }

    @Override
    public Object getKey() {
        return key;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MethodParameterNode [name=");
        builder.append(name);
        builder.append("]");
        return builder.toString();
    }
}
