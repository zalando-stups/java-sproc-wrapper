package org.zalando.sprocwrapper.proxy;

import com.google.common.base.MoreObjects;
import com.google.common.reflect.AbstractInvocationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zalando.sprocwrapper.dsprovider.DataSourceProvider;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * @author  jmussler
 */
class SProcProxy extends AbstractInvocationHandler {

    private final HashMap<Method, StoredProcedure> sprocs = new HashMap<Method, StoredProcedure>();
    private final DataSourceProvider dataSourceProvider;

    private static final Logger LOG = LoggerFactory.getLogger(SProcProxy.class);
    private final String description;

    public boolean addStoredProcedure(final Method method, final StoredProcedure p) {
        if (sprocs.containsKey(method)) {
            return false;
        }

        sprocs.put(method, p);
        return true;
    }

    public SProcProxy(final DataSourceProvider provider, final String description) {
        if (provider == null) {
            throw new IllegalArgumentException("DataSourceProvider cannot be null");
        }

        dataSourceProvider = provider;
        this.description = description;
    }

    @Override
    protected Object handleInvocation(final Object proxy, final Method method, final Object[] args) {
        final StoredProcedure p = sprocs.get(method);

        if (p == null) {
            LOG.warn("no StoredProcedure found for method {}", method);
            return null;
        }

        return p.execute(dataSourceProvider, new InvocationContext(proxy, method, args));
    }

    @Override
    public String toString() {
        return
            MoreObjects.toStringHelper(this)                          //
                       .addValue(description)                         //
                       .add("dataSourceProvider", dataSourceProvider) //
                       .toString();
    }
}
