package de.zalando.sprocwrapper.validation;

import java.lang.reflect.Method;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.validation.Path;

import com.google.common.base.Preconditions;

public class SimplePath implements Path {

    public final List<Path.Node> nodeList = new LinkedList<Path.Node>();

    public void addNode(final Path.Node node) {
        nodeList.add(Preconditions.checkNotNull(node, "node"));
    }

    @Override
    public Iterator<Node> iterator() {
        return nodeList.iterator();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SimplePath [nodeList=");
        builder.append(nodeList);
        builder.append("]");
        return builder.toString();
    }

    public static SimplePath createPathForMethodParameter(final Method method, final String parameterName) {
        Preconditions.checkNotNull(method, "method");
        Preconditions.checkNotNull(parameterName, "parameterName");

        final StringBuilder builder = new StringBuilder(method.getDeclaringClass().getSimpleName());
        builder.append("#").append(method.getName()).append("(").append(parameterName).append(")");

        SimplePath path = new SimplePath();
        path.addNode(new SimpleNode(builder.toString(), false, null, null));

        return path;
    }

}
