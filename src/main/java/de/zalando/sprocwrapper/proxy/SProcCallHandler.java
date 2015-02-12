package de.zalando.sprocwrapper.proxy;

import de.zalando.sprocwrapper.SProcCall;
import de.zalando.sprocwrapper.SProcParam;
import de.zalando.sprocwrapper.SProcService;
import de.zalando.sprocwrapper.sharding.ShardKey;
import de.zalando.sprocwrapper.sharding.VirtualShardKeyStrategy;
import de.zalando.sprocwrapper.util.NameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author Soroosh Sarabadani
 */

public class SProcCallHandler {
    private static final Logger LOG = LoggerFactory.getLogger(SProcCallHandler.class);

    private static String getSqlNameForMethod(final String methodName) {
        return NameUtils.camelCaseToUnderscore(methodName);
    }

    List<Method> findSProcCallAnnotatedMethods(Class c) {
        List<Method> foundMethods = new ArrayList<>();
        for (Method method : c.getMethods()) {
            if (method.isAnnotationPresent(SProcCall.class)) {
                foundMethods.add(method);
            }
        }
        return foundMethods;

    }

    private RowMapper getRowMapper(SProcCall scA) {
        if (scA.resultMapper() != Void.class) {
            try {
                return (RowMapper<?>) scA.resultMapper().newInstance();
            } catch (final InstantiationException | IllegalAccessException ex) {
                LOG.error("Result mapper for sproc can not be instantiated", ex);
                throw new IllegalArgumentException("Result mapper for sproc can not be instantiated");
            }
        }
        return null;

    }

    private boolean isValidationActive(SProcCall scA, SProcServiceAnnotationHandler.HandlerResult handlerResult) {
        boolean result = handlerResult.isValidationActive();

        // overwrite if explicitly set in SprocCall:
        if (scA.validate() == SProcCall.Validate.YES) {
            result = true;
        } else if (scA.validate() == SProcCall.Validate.NO) {
            result = false;
        }
        return result;
    }

    private SProcService.WriteTransaction getWriteTransaction(SProcCall scA, SProcServiceAnnotationHandler.HandlerResult handlerResult) {
        SProcService.WriteTransaction serviceWriteTransaction = handlerResult.getWriteTransaction();
        serviceWriteTransaction = scA.shardedWriteTransaction().getServiceWriteTransaction(serviceWriteTransaction);
        return serviceWriteTransaction;
    }

    public Map<Method, StoredProcedure> handle(Class c, SProcServiceAnnotationHandler.HandlerResult handlerResult) {
        if (handlerResult == null) {
            throw new IllegalArgumentException("handlerResult should not be null");
        }

        if (c == null) {
            throw new IllegalArgumentException("class should not be null");
        }

        Map<Method, StoredProcedure> result = new HashMap<>();
        List<Method> annotatedMethods = this.findSProcCallAnnotatedMethods(c);

        for (Method method : annotatedMethods) {
            final SProcCall scA = method.getAnnotation(SProcCall.class);

            String name = scA.name();
            if ("".equals(name)) {
                name = getSqlNameForMethod(method.getName());
            }

            name = handlerResult.getPrefix() + name;

            VirtualShardKeyStrategy sprocStrategy = handlerResult.getShardKeyStrategy();
            if (scA.shardStrategy() != Void.class) {
                try {
                    sprocStrategy = (VirtualShardKeyStrategy) scA.shardStrategy().newInstance();
                } catch (final InstantiationException | IllegalAccessException ex) {
                    LOG.error("Shard strategy for sproc can not be instantiated", ex);
                    throw new IllegalArgumentException("Shard strategy for sproc can not be instantiated");
                }
            }

            RowMapper<?> resultMapper = this.getRowMapper(scA);

            boolean useValidation = this.isValidationActive(scA, handlerResult);
            final StoredProcedure storedProcedure = this.getStoredProcedure(scA, handlerResult, method, name, sprocStrategy, resultMapper, useValidation);

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
                            throw new IllegalArgumentException("Could not instantiate StoredProcedureParameter. ABORTING.");
                        }
                    }
                }

                pos++;
            }
            result.put(method, storedProcedure);
        }
        return result;
    }

    private StoredProcedure getStoredProcedure(SProcCall scA, SProcServiceAnnotationHandler.HandlerResult handlerResult, Method method, String name, VirtualShardKeyStrategy sprocStrategy, RowMapper<?> resultMapper, boolean useValidation) {
        try {
            SProcService.WriteTransaction writeTransaction = this.getWriteTransaction(scA, handlerResult);

            StoredProcedure storedProcedure = new StoredProcedure(name, method.getGenericReturnType(), sprocStrategy,
                    scA.runOnAllShards(), scA.searchShards(), scA.parallel(), resultMapper,
                    scA.timeoutInMilliSeconds(), scA.adivsoryLockType(), useValidation, scA.readOnly(),
                    writeTransaction);
            if (!"".equals(scA.sql())) {
                storedProcedure.setQuery(scA.sql());
            }
            return storedProcedure;
        } catch (final InstantiationException | IllegalAccessException e) {
            LOG.error("Could not instantiate StoredProcedure. ABORTING.", e);
            throw new IllegalArgumentException("Could not instantiate StoredProcedure. ABORTING.");
        }

    }


}
