package org.zalando.sprocwrapper.proxy;

import org.zalando.sprocwrapper.SProcCall;
import org.zalando.sprocwrapper.SProcParam;
import org.zalando.sprocwrapper.SProcService;
import org.zalando.sprocwrapper.sharding.ShardKey;
import org.zalando.sprocwrapper.sharding.VirtualShardKeyStrategy;
import org.zalando.sprocwrapper.util.NameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
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
                return (RowMapper<?>) scA.resultMapper().getDeclaredConstructor().newInstance();
            } catch (final InstantiationException | IllegalAccessException | SecurityException | InvocationTargetException | IllegalArgumentException | NoSuchMethodException ex) {
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

    public static SProcService.WriteTransaction mapSprocWriteTransactionToServiceWriteTransaction(SProcCall.WriteTransaction scWiWriteTransaction, SProcServiceAnnotationHandler.HandlerResult handlerResult) {
        SProcService.WriteTransaction serviceWriteTransaction = handlerResult.getWriteTransaction();
        if (scWiWriteTransaction == null) {
            throw new IllegalArgumentException("scWiWriteTransaction cannot be null");
        }

        switch (scWiWriteTransaction) {
            case NONE:
                serviceWriteTransaction = SProcService.WriteTransaction.NONE;
                break;
            case ONE_PHASE:
                serviceWriteTransaction = SProcService.WriteTransaction.ONE_PHASE;
                break;
            case TWO_PHASE:
                serviceWriteTransaction = SProcService.WriteTransaction.TWO_PHASE;
                break;
            default:
                if (serviceWriteTransaction == null) {
                    throw new IllegalArgumentException("ServiceWriteTransaction cannot be null");
                }
                break;
        }
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
                    sprocStrategy = (VirtualShardKeyStrategy) scA.shardStrategy().getDeclaredConstructor().newInstance();
                } catch (final InstantiationException | IllegalAccessException | SecurityException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException ex) {
                    LOG.error("Shard strategy for sproc can not be instantiated", ex);
                    throw new IllegalArgumentException("Shard strategy for sproc can not be instantiated");
                }
            }

            RowMapper<?> resultMapper = this.getRowMapper(scA);

            final boolean useValidation = this.isValidationActive(scA, handlerResult);

            final List<ShardKeyParameter> shardKeyParameters = new ArrayList<>();
            final List<StoredProcedureParameter> params = new ArrayList<>();

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
                        shardKeyParameters.add(new ShardKeyParameter(pos, clazz));
                    }

                    if (a instanceof SProcParam) {
                        final SProcParam sParam = (SProcParam) a;

                        final String dbTypeName = sParam.type();

                        try {
                            params.add(StoredProcedureParameter.createParameter(clazz, genericType,
                                    method, dbTypeName, sParam.sqlType(), pos, sParam.sensitive()));
                        } catch (final InstantiationException | IllegalAccessException | NoSuchMethodException | IllegalArgumentException | SecurityException | InvocationTargetException e) {
                            LOG.error("Could not instantiate StoredProcedureParameter. ABORTING.", e);
                            throw new IllegalArgumentException("Could not instantiate StoredProcedureParameter. ABORTING.");
                        }
                    }
                }

                pos++;
            }
            final StoredProcedure storedProcedure = createStoredProcedure(scA, handlerResult, method, name, params, sprocStrategy, shardKeyParameters, resultMapper, useValidation);

            result.put(method, storedProcedure);
        }
        return result;
    }

    private StoredProcedure createStoredProcedure(SProcCall scA, SProcServiceAnnotationHandler.HandlerResult handlerResult,
        Method method, String name, List<StoredProcedureParameter> params,
        VirtualShardKeyStrategy sprocStrategy, List<ShardKeyParameter> shardKeyParameters,
        RowMapper<?> resultMapper, boolean useValidation) {
        try {
            SProcService.WriteTransaction writeTransaction = mapSprocWriteTransactionToServiceWriteTransaction(scA.shardedWriteTransaction(), handlerResult);

            String query = !"".equals(scA.sql()) ? scA.sql() : null;

            StoredProcedure storedProcedure = new StoredProcedure(name, query, params, method.getGenericReturnType(), sprocStrategy, shardKeyParameters,
                    scA.runOnAllShards(), scA.searchShards(), scA.parallel(), resultMapper,
                    scA.timeoutInMilliSeconds(), new SProcCall.AdvisoryLock(scA.adivsoryLockName(),scA.adivsoryLockId()), useValidation, scA.readOnly(),
                    writeTransaction);

            return storedProcedure;
        } catch (final InstantiationException | IllegalAccessException | NoSuchMethodException | IllegalArgumentException | SecurityException | InvocationTargetException e) {
            LOG.error("Could not instantiate StoredProcedure. ABORTING.", e);
            throw new IllegalArgumentException("Could not instantiate StoredProcedure. ABORTING.");
        }

    }


}
