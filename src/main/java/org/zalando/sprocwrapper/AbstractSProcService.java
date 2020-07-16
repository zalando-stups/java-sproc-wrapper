package org.zalando.sprocwrapper;

import org.zalando.sprocwrapper.dsprovider.DataSourceProvider;
import org.zalando.sprocwrapper.proxy.SProcProxyBuilder;

/**
 * @author  jmussler
 */
public abstract class AbstractSProcService<I, P extends DataSourceProvider> {

    protected P ds;

    protected I sproc;

    protected Class<I> interfaceClass;

    protected AbstractSProcService(final P ps, final Class<I> clazz) {
        interfaceClass = clazz;
        ds = ps;
        sproc = SProcProxyBuilder.build(ds, interfaceClass);
    }
}
