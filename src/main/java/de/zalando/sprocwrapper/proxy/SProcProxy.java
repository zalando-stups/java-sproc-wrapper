package de.zalando.sprocwrapper.proxy;

import java.lang.reflect.Method;

import java.util.HashMap;

import org.apache.log4j.Logger;

import de.zalando.sprocwrapper.dsprovider.DataSourceProvider;

/**
 * @author  jmussler
 */
class SProcProxy implements java.lang.reflect.InvocationHandler {

    private final HashMap<Method, StoredProcedure> sprocs = new HashMap<Method, StoredProcedure>();
    private final DataSourceProvider dataSourceProvider;

    private static final Logger LOG = Logger.getLogger(SProcProxy.class);

    public boolean addStoredProcedure(final Method method, final StoredProcedure p) {
        if (sprocs.containsKey(method)) {
            return false;
        }

        sprocs.put(method, p);
        return true;
    }

    public SProcProxy(final DataSourceProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("DataSourceProvider cannot be null");
        }

        dataSourceProvider = provider;
    }

    @Override
    public Object invoke(final Object proxy, final Method m, final Object[] args) {
        StoredProcedure p = sprocs.get(m);

        if (p == null) {
            LOG.warn("no StoredProcedure found for method " + m);
            return null;
        }

        return p.execute(dataSourceProvider, args);
    }
}
