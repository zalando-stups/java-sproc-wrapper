package de.zalando.sprocwrapper.proxy;

import java.lang.reflect.Method;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.zalando.sprocwrapper.dsprovider.DataSourceProvider;

/**
 * @author jmussler
 */
public class SProcProxyBuilder {

    private static SProcServiceAnnotationHandler sProcServiceAnnotationHandler = new SProcServiceAnnotationHandler();
    private static SProcCallHandler sProcCallHandler = new SProcCallHandler();

    private static final Logger LOG = LoggerFactory.getLogger(SProcProxyBuilder.class);

    private SProcProxyBuilder() {
        // utility class: private constructor
    }

    @SuppressWarnings("unchecked")
    public static <T> T build(final DataSourceProvider d, final Class<T> c) {
        final SProcProxy proxy = new SProcProxy(d);


        try {
            SProcServiceAnnotationHandler.HandlerResult handlerResult = sProcServiceAnnotationHandler.handle(c);
            Map<Method, StoredProcedure> spMap = sProcCallHandler.handle(c, handlerResult);
            for (Method method : spMap.keySet()) {
                StoredProcedure storedProcedure = spMap.get(method);
                LOG.debug("{} registering {}", c.getSimpleName(), storedProcedure);
                proxy.addStoredProcedure(method, storedProcedure);
            }
        } catch (IllegalArgumentException ex) {
            return null;
        }
        return (T) java.lang.reflect.Proxy.newProxyInstance(c.getClassLoader(), new Class[]{c}, proxy);
    }

}
