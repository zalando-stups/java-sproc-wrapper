package de.zalando.sprocwrapper.proxy;

import java.lang.reflect.Method;

import java.util.Arrays;

import com.google.common.base.Preconditions;

public class InvocationContext {

    private final Object proxy;
    private final Method method;
    private final Object[] args;

    public InvocationContext(final Object proxy, final Method method, final Object[] args) {
        this.proxy = Preconditions.checkNotNull(proxy, "proxy");
        this.method = Preconditions.checkNotNull(method, "method");
        this.args = args;
    }

    public Object getProxy() {
        return proxy;
    }

    public Method getMethod() {
        return method;
    }

    public Object[] getArgs() {
        return args;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("InvocationContext [proxy=");
        builder.append(proxy);
        builder.append(", method=");
        builder.append(method);
        builder.append(", args=");
        builder.append(Arrays.toString(args));
        builder.append("]");
        return builder.toString();
    }

}
