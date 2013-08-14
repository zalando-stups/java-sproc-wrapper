package de.zalando.sprocwrapper.proxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.RowMapper;

import de.zalando.sprocwrapper.SProcCall;
import de.zalando.sprocwrapper.SProcCall.Validate;
import de.zalando.sprocwrapper.SProcParam;
import de.zalando.sprocwrapper.SProcService;
import de.zalando.sprocwrapper.SProcService.WriteTransaction;
import de.zalando.sprocwrapper.dsprovider.DataSourceProvider;
import de.zalando.sprocwrapper.sharding.ShardKey;
import de.zalando.sprocwrapper.sharding.VirtualShardKeyStrategy;
import de.zalando.sprocwrapper.util.NameUtils;

/**
 * @author  jmussler
 */
public class SProcProxyBuilder {

    private static final VirtualShardKeyStrategy VIRTUAL_SHARD_KEY_STRATEGY_DEFAULT = new VirtualShardKeyStrategy();

    private static final Logger LOG = LoggerFactory.getLogger(SProcProxyBuilder.class);

    private SProcProxyBuilder() {
        // utility class: private constructor
    }

    private static String getSqlNameForMethod(final String methodName) {
        return NameUtils.camelCaseToUnderscore(methodName);
    }

    @SuppressWarnings("unchecked")
    public static <T> T build(final DataSourceProvider d, final Class<T> c) {
        final Method[] methods = c.getMethods();

        final SProcProxy proxy = new SProcProxy(d);

        final SProcService serviceAnnotation = c.getAnnotation(SProcService.class);
        VirtualShardKeyStrategy keyStrategy = VIRTUAL_SHARD_KEY_STRATEGY_DEFAULT;
        String prefix = "";
        if (serviceAnnotation != null) {
            try {
                keyStrategy = (VirtualShardKeyStrategy) serviceAnnotation.shardStrategy().newInstance();
            } catch (final InstantiationException | IllegalAccessException ex) {
                LOG.error("ShardKey strategy for service can not be instantiated", ex);
                return null;
            }

            if (!"".equals(serviceAnnotation.namespace())) {
                prefix = serviceAnnotation.namespace() + "_";
            }
        }

        for (final Method method : methods) {
            final SProcCall scA = method.getAnnotation(SProcCall.class);

            if (scA == null) {
                continue;
            }

            String name = scA.name();
            if ("".equals(name)) {
                name = getSqlNameForMethod(method.getName());
            }

            name = prefix + name;

            VirtualShardKeyStrategy sprocStrategy = keyStrategy;
            if (scA.shardStrategy() != Void.class) {
                try {
                    sprocStrategy = (VirtualShardKeyStrategy) scA.shardStrategy().newInstance();
                } catch (final InstantiationException | IllegalAccessException ex) {
                    LOG.error("Shard strategy for sproc can not be instantiated", ex);
                    return null;
                }
            }

            RowMapper<?> resultMapper = null;

            if (scA.resultMapper() != Void.class) {
                try {
                    resultMapper = (RowMapper<?>) scA.resultMapper().newInstance();
                } catch (final InstantiationException | IllegalAccessException ex) {
                    LOG.error("Result mapper for sproc can not be instantiated", ex);
                    return null;
                }
            }

            boolean useValidation;
            if (serviceAnnotation != null) {

                // take validation settings from SProcService annotation:
                useValidation = serviceAnnotation.validate();
            } else {
                useValidation = false;
            }

            // overwrite if explicitly set in SprocCall:
            if (scA.validate() == Validate.YES) {
                useValidation = true;
            } else if (scA.validate() == Validate.NO) {
                useValidation = false;
            }

            final StoredProcedure storedProcedure;
            try {
                WriteTransaction writeTransaction = getWriteTransactionServiceAnnotation(serviceAnnotation);
                if (scA.shardedWriteTransaction()
                        != de.zalando.sprocwrapper.SProcCall.WriteTransaction.USE_FROM_SERVICE) {
                    switch (scA.shardedWriteTransaction()) {

                        case NONE :
                            writeTransaction = WriteTransaction.NONE;
                            break;

                        case ONE_PHASE :
                            writeTransaction = WriteTransaction.ONE_PHASE;
                            break;

                        case TWO_PHASE :
                            writeTransaction = WriteTransaction.TWO_PHASE;
                            break;

                        case USE_FROM_SERVICE :
                            writeTransaction = getWriteTransactionServiceAnnotation(serviceAnnotation);
                    }
                }

                storedProcedure = new StoredProcedure(name, method.getGenericReturnType(), sprocStrategy,
                        scA.runOnAllShards(), scA.searchShards(), scA.parallel(), resultMapper,
                        scA.timeoutInMilliSeconds(), scA.adivsoryLockType(), useValidation, scA.readOnly(),
                        writeTransaction);
                if (!"".equals(scA.sql())) {
                    storedProcedure.setQuery(scA.sql());
                }
            } catch (final InstantiationException | IllegalAccessException e) {
                LOG.error("Could not instantiate StoredProcedure. ABORTING.", e);
                return null;
            }

            int pos = 0;
            for (final Annotation[] as : method.getParameterAnnotations()) {

                for (final Annotation a : as) {
                    final Class<?> clazz = method.getParameterTypes()[pos];
                    Type genericType = method.getGenericParameterTypes()[pos];
                    if (genericType instanceof ParameterizedType) {
                        final ParameterizedType parameterizedType = (ParameterizedType) genericType;
                        if (parameterizedType.getActualTypeArguments() != null
                                && parameterizedType.getActualTypeArguments().length > 0) {
                            genericType = parameterizedType.getActualTypeArguments()[0];
                        }
                    }

                    if (a instanceof ShardKey) {
                        storedProcedure.addShardKeyParameter(pos, clazz);
                    }

                    if (a instanceof SProcParam) {
                        final SProcParam sParam = (SProcParam) a;

                        final String dbTypeName = sParam.type();

                        try {
                            storedProcedure.addParam(StoredProcedureParameter.createParameter(clazz, genericType,
                                    method, dbTypeName, sParam.sqlType(), pos, sParam.sensitive()));
                        } catch (final InstantiationException | IllegalAccessException e) {
                            LOG.error("Could not instantiate StoredProcedureParameter. ABORTING.", e);
                            return null;
                        }
                    }
                }

                pos++;
            }

            LOG.debug("{} registering {}", c.getSimpleName(), storedProcedure);
            proxy.addStoredProcedure(method, storedProcedure);
        }

        return (T) java.lang.reflect.Proxy.newProxyInstance(c.getClassLoader(), new Class[] {c}, proxy);
    }

    private static WriteTransaction getWriteTransactionServiceAnnotation(final SProcService serviceAnnotation) {
        return serviceAnnotation == null ? WriteTransaction.NONE : serviceAnnotation.shardedWriteTransaction();
    }
}
