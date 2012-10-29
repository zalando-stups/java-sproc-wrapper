package de.zalando.sprocwrapper.proxy;

import java.lang.reflect.ParameterizedType;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.RowMapper;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.typemapper.core.ValueTransformer;

import de.zalando.sprocwrapper.SProcCall.AdvisoryLock;
import de.zalando.sprocwrapper.SProcService.WriteTransaction;
import de.zalando.sprocwrapper.dsprovider.DataSourceProvider;
import de.zalando.sprocwrapper.dsprovider.SameConnectionDatasource;
import de.zalando.sprocwrapper.globalvaluetransformer.GlobalValueTransformerLoader;
import de.zalando.sprocwrapper.proxy.executors.Executor;
import de.zalando.sprocwrapper.proxy.executors.ExecutorWrapper;
import de.zalando.sprocwrapper.proxy.executors.GlobalTransformerExecutorWrapper;
import de.zalando.sprocwrapper.proxy.executors.MultiRowSimpleTypeExecutor;
import de.zalando.sprocwrapper.proxy.executors.MultiRowTypeMapperExecutor;
import de.zalando.sprocwrapper.proxy.executors.SingleRowCustomMapperExecutor;
import de.zalando.sprocwrapper.proxy.executors.SingleRowSimpleTypeExecutor;
import de.zalando.sprocwrapper.proxy.executors.SingleRowTypeMapperExecutor;
import de.zalando.sprocwrapper.proxy.executors.ValidationExecutorWrapper;
import de.zalando.sprocwrapper.sharding.ShardedObject;
import de.zalando.sprocwrapper.sharding.VirtualShardKeyStrategy;

/**
 * @author  jmussler
 */
class StoredProcedure {

    private static final int TRUNCATE_DEBUG_PARAMS_MAX_LENGTH = 1024;
    private static final String TRUNCATE_DEBUG_PARAMS_ELLIPSIS = " ...";

    private static final Logger LOG = LoggerFactory.getLogger(StoredProcedure.class);

    private final List<StoredProcedureParameter> params = new ArrayList<StoredProcedureParameter>();

    private final String name;
    private String query = null;
    private Class<?> returnType = null;

    // whether the result type is a collection (List)
    private boolean collectionResult = false;
    private final boolean runOnAllShards;
    private final boolean searchShards;
    private boolean autoPartition;
    private final boolean parallel;
    private final boolean readOnly;
    private final WriteTransaction writeTransaction;

    private Executor executor = null;

    private VirtualShardKeyStrategy shardStrategy;
    private List<ShardKeyParameter> shardKeyParameters = null;
    private final RowMapper<?> resultMapper;

    private int[] types = null;

    private static final Executor MULTI_ROW_SIMPLE_TYPE_EXECUTOR = new MultiRowSimpleTypeExecutor();
    private static final Executor MULTI_ROW_TYPE_MAPPER_EXECUTOR = new MultiRowTypeMapperExecutor();
    private static final Executor SINGLE_ROW_SIMPLE_TYPE_EXECUTOR = new SingleRowSimpleTypeExecutor();
    private static final Executor SINGLE_ROW_TYPE_MAPPER_EXECUTOR = new SingleRowTypeMapperExecutor();

    private final long timeout;
    private final AdvisoryLock adivsoryLock;

    public StoredProcedure(final String name, final java.lang.reflect.Type genericType,
            final VirtualShardKeyStrategy sStrategy, final boolean runOnAllShards, final boolean searchShards,
            final boolean parallel, final RowMapper<?> resultMapper, final long timeout,
            final AdvisoryLock advisoryLock, final boolean useValidation, final boolean readOnly,
            final WriteTransaction writeTransaction) throws InstantiationException, IllegalAccessException {
        this.name = name;
        this.runOnAllShards = runOnAllShards;
        this.searchShards = searchShards;
        this.parallel = parallel;
        this.readOnly = readOnly;
        this.resultMapper = resultMapper;
        this.writeTransaction = writeTransaction;

        this.adivsoryLock = advisoryLock;
        this.timeout = timeout;

        shardStrategy = sStrategy;

        ValueTransformer<?, ?> valueTransformerForClass = null;

        if (genericType instanceof ParameterizedType) {
            final ParameterizedType pType = (ParameterizedType) genericType;

            if (java.util.List.class.isAssignableFrom((Class<?>) pType.getRawType())
                    && pType.getActualTypeArguments().length > 0) {
                returnType = (Class<?>) pType.getActualTypeArguments()[0];

                // check if we have a value transformer (and initialize the registry):
                valueTransformerForClass = GlobalValueTransformerLoader.getValueTransformerForClass(returnType);

                if (valueTransformerForClass != null
                        || SingleRowSimpleTypeExecutor.SIMPLE_TYPES.containsKey(returnType)) {
                    executor = MULTI_ROW_SIMPLE_TYPE_EXECUTOR;
                } else {
                    executor = MULTI_ROW_TYPE_MAPPER_EXECUTOR;
                }

                collectionResult = true;
            } else {
                executor = SINGLE_ROW_TYPE_MAPPER_EXECUTOR;
                returnType = (Class<?>) pType.getRawType();
            }

        } else {
            returnType = (Class<?>) genericType;

            // check if we have a value transformer (and initialize the registry):
            valueTransformerForClass = GlobalValueTransformerLoader.getValueTransformerForClass(returnType);

            if (valueTransformerForClass != null || SingleRowSimpleTypeExecutor.SIMPLE_TYPES.containsKey(returnType)) {
                executor = SINGLE_ROW_SIMPLE_TYPE_EXECUTOR;
            } else {
                if (resultMapper != null) {
                    executor = new SingleRowCustomMapperExecutor(resultMapper);
                } else {
                    executor = SINGLE_ROW_TYPE_MAPPER_EXECUTOR;
                }
            }
        }

        if (this.timeout > 0 || this.adivsoryLock != AdvisoryLock.NO_LOCK) {

            // Wrapper provides locking and changing of session settings functionality
            this.executor = new ExecutorWrapper(executor, this.timeout, this.adivsoryLock);
        }

        if (useValidation) {
            this.executor = new ValidationExecutorWrapper(this.executor);
        }

        if (valueTransformerForClass != null) {

            // we need to transform the return value by the global value transformer.
            // add the transformation to the as a transformerExecutor
            this.executor = new GlobalTransformerExecutorWrapper(this.executor);
        }
    }

    public void addParam(final StoredProcedureParameter p) {
        params.add(p);
    }

    public void setVirtualShardKeyStrategy(final VirtualShardKeyStrategy s) {
        shardStrategy = s;
    }

    public void addShardKeyParameter(final int jp, final Class<?> clazz) {
        if (shardKeyParameters == null) {
            shardKeyParameters = new ArrayList<ShardKeyParameter>(1);
        }

        if (List.class.isAssignableFrom(clazz)) {
            autoPartition = true;
        }

        shardKeyParameters.add(new ShardKeyParameter(jp));
    }

    public String getName() {
        return name;
    }

    public Object[] getParams(final Object[] origParams, final Connection connection) {
        final Object[] ps = new Object[params.size()];

        int i = 0;
        for (final StoredProcedureParameter p : params) {
            try {
                ps[i] = p.mapParam(origParams[p.getJavaPos()], connection);
            } catch (final Exception e) {
                final String errorMessage = "Could not map input parameter for stored procedure " + name + " of type "
                        + p.getType() + " at position " + p.getJavaPos() + ": "
                        + (p.isSensitive() ? "<SENSITIVE>" : origParams[p.getJavaPos()]);
                LOG.error(errorMessage, e);
                throw new IllegalArgumentException(errorMessage, e);
            }

            i++;
        }

        return ps;
    }

    public int[] getTypes() {
        if (types == null) {
            types = new int[params.size()];

            int i = 0;
            for (final StoredProcedureParameter p : params) {
                types[i++] = p.getType();
            }
        }

        return types;
    }

    public int getShardId(final Object[] objs) {
        if (shardKeyParameters == null) {
            return shardStrategy.getShardId(null);
        }

        final Object[] keys = new Object[shardKeyParameters.size()];
        int i = 0;
        Object obj;
        for (final ShardKeyParameter p : shardKeyParameters) {
            obj = objs[p.javaPos];
            if (obj instanceof ShardedObject) {
                obj = ((ShardedObject) obj).getShardKey();
            }

            keys[i] = obj;
            i++;
        }

        return shardStrategy.getShardId(keys);
    }

    public String getSqlParameterList() {
        String s = "";
        boolean first = true;
        for (int i = 1; i <= params.size(); ++i) {
            if (!first) {
                s += ",";
            }

            first = false;

            s += "?";
        }

        return s;
    }

    public void setQuery(final String sql) {
        query = sql;
    }

    public String getQuery() {
        if (query == null) {
            query = "SELECT * FROM " + name + " ( " + getSqlParameterList() + " )";
        }

        return query;
    }

    /**
     * build execution string like create_or_update_multiple_objects({"(a,b)","(c,d)" }).
     *
     * @param   args
     *
     * @return
     */
    private String getDebugLog(final Object[] args) {
        final StringBuilder sb = new StringBuilder(name);
        sb.append('(');

        int i = 0;
        for (final Object param : args) {
            if (i > 0) {
                sb.append(',');
            }

            if (param == null) {
                sb.append("NULL");
            } else if (params.get(i).isSensitive()) {
                sb.append("<SENSITIVE>");
            } else {
                sb.append(param);
            }

            i++;
            if (sb.length() > TRUNCATE_DEBUG_PARAMS_MAX_LENGTH) {
                break;
            }
        }

        if (sb.length() > TRUNCATE_DEBUG_PARAMS_MAX_LENGTH) {

            // Truncate params for debug output
            return sb.substring(0, TRUNCATE_DEBUG_PARAMS_MAX_LENGTH) + TRUNCATE_DEBUG_PARAMS_ELLIPSIS + ")";
        } else {
            sb.append(')');
            return sb.toString();
        }
    }

    /**
     * split arguments by shard.
     *
     * @param   dataSourceProvider
     * @param   args                the original argument list
     *
     * @return  map of virtual shard ID to argument list (TreeMap with ordered keys: sorted by shard ID)
     */
    @SuppressWarnings("unchecked")
    private Map<Integer, Object[]> partitionArguments(final DataSourceProvider dataSourceProvider,
            final Object[] args) {

        // use TreeMap here to maintain ordering by shard ID
        final Map<Integer, Object[]> argumentsByShardId = Maps.newTreeMap();

        // we need to partition by datasource instead of virtual shard ID (different virtual shard IDs are mapped to
        // the same datasource e.g. by VirtualShardMd5Strategy)
        final Map<DataSource, Integer> shardIdByDataSource = Maps.newHashMap();

        // TODO: currently only implemented for single shardKey argument as first argument!
        final List<Object> originalArgument = (List<Object>) args[0];
        List<Object> partitionedArgument = null;
        Object[] partitionedArguments = null;
        int shardId;
        Integer existingShardId;
        DataSource dataSource;
        for (final Object key : originalArgument) {
            shardId = getShardId(new Object[] {key});
            dataSource = dataSourceProvider.getDataSource(shardId);
            existingShardId = shardIdByDataSource.get(dataSource);
            if (existingShardId != null) {

                // we already saw the same datasource => use the virtual shard ID of the first argument with the same
                // datasource
                shardId = existingShardId;
            } else {
                shardIdByDataSource.put(dataSource, shardId);
            }

            partitionedArguments = argumentsByShardId.get(shardId);
            if (partitionedArguments == null) {

                partitionedArgument = Lists.newArrayList();
                partitionedArguments = new Object[args.length];
                partitionedArguments[0] = partitionedArgument;
                if (args.length > 1) {
                    System.arraycopy(args, 1, partitionedArguments, 1, args.length - 1);
                }

                argumentsByShardId.put(shardId, partitionedArguments);
            } else {
                partitionedArgument = (List<Object>) partitionedArguments[0];
            }

            partitionedArgument.add(key);

        }

        return argumentsByShardId;
    }

    private static class Call implements Callable<Object> {
        private final StoredProcedure sproc;
        private final DataSource shardDs;
        private final Object[] params;
        private final Object[] originalArgs;

        public Call(final StoredProcedure sproc, final DataSource shardDs, final Object[] params,
                final Object[] originalArgs) {
            this.sproc = sproc;
            this.shardDs = shardDs;
            this.params = params;
            this.originalArgs = originalArgs;
        }

        @Override
        public Object call() throws Exception {
            return sproc.executor.executeSProc(shardDs, sproc.getQuery(), params, sproc.getTypes(), originalArgs,
                    sproc.returnType);
        }

    }

    private static ExecutorService parallelThreadPool = Executors.newCachedThreadPool();

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object execute(final DataSourceProvider dp, final Object[] args) {

        List<Integer> shardIds = null;
        Map<Integer, Object[]> partitionedArguments = null;
        if (runOnAllShards || searchShards) {

            shardIds = dp.getDistinctShardIds();
        } else {
            if (autoPartition) {
                partitionedArguments = partitionArguments(dp, args);
                shardIds = Lists.newArrayList(partitionedArguments.keySet());
            } else {
                shardIds = Lists.newArrayList(getShardId(args));
            }
        }

        if (partitionedArguments == null) {
            partitionedArguments = Maps.newHashMap();
            for (final int shardId : shardIds) {
                partitionedArguments.put(shardId, args);
            }
        }

        final DataSource firstDs = dp.getDataSource(shardIds.get(0));
        Connection connection = null;
        try {
            connection = firstDs.getConnection();

        } catch (final SQLException e) {
            throw new IllegalStateException("Failed to acquire connection for virtual shard " + shardIds.get(0)
                    + " for " + name, e);
        }

        final List<Object[]> paramValues = Lists.newArrayList();
        try {
            for (final int shardId : shardIds) {
                paramValues.add(getParams(partitionedArguments.get(shardId), connection));
            }

        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (final Throwable t) {
                    LOG.warn("Could not release connection", t);
                }
            }
        }

        if (shardIds.size() == 1 && !autoPartition) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(getDebugLog(paramValues.get(0)));
            }

            // most common case: only one shard and no argument partitioning
            return executor.executeSProc(firstDs, getQuery(), paramValues.get(0), getTypes(), args, returnType);
        } else {
            Map<Integer, SameConnectionDatasource> transactionalDatasources = null;
            try {

                // we may need to start a transaction context
                transactionalDatasources = startTransaction(dp, shardIds);

                final List<?> results = Lists.newArrayList();
                Object sprocResult = null;
                final long start = System.currentTimeMillis();
                if (parallel) {
                    sprocResult = executeInParallel(dp, args, shardIds, paramValues, transactionalDatasources, results,
                            sprocResult);
                } else {
                    sprocResult = executeSequential(dp, args, shardIds, paramValues, transactionalDatasources, results,
                            sprocResult);
                }

                if (LOG.isTraceEnabled()) {
                    LOG.trace("[{}] execution of [{}] on [{}] shards took [{}] ms",
                        new Object[] {
                            parallel ? "parallel" : "serial", name, shardIds.size(), System.currentTimeMillis() - start
                        });
                }

                // no error - we may need to commit
                commitTransaction(transactionalDatasources);

                if (collectionResult) {
                    return results;
                } else {

                    // return last result
                    return sprocResult;
                }

            } catch (final RuntimeException runtimeException) {

                LOG.trace("[{}] execution of [{}] on [{}] shards aborted by runtime exception [{}]",
                    new Object[] {
                        parallel ? "parallel" : "serial", name, shardIds.size(), runtimeException.getMessage(),
                        runtimeException
                    });

                // error occured, we may need to rollback all transactions.
                rollbackTransaction(transactionalDatasources);

                // re-throw
                throw runtimeException;
            } catch (final Throwable throwable) {

                LOG.trace("[{}] execution of [{}] on [{}] shards aborted by throwable exception [{}]",
                    new Object[] {
                        parallel ? "parallel" : "serial", name, shardIds.size(), throwable.getMessage(), throwable
                    });

                // error occured, we may need to rollback all transactions.
                rollbackTransaction(transactionalDatasources);

                // throw runtime:
                throw new RuntimeException(throwable);
            }

        }

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Object executeSequential(final DataSourceProvider dp, final Object[] args, final List<Integer> shardIds,
            final List<Object[]> paramValues, final Map<Integer, SameConnectionDatasource> transactionalDatasources,
            final List<?> results, Object sprocResult) {
        DataSource shardDs;
        int i = 0;
        final List<String> exceptions = Lists.newArrayList();
        for (final int shardId : shardIds) {
            shardDs = getShardDs(dp, transactionalDatasources, shardId);
            if (LOG.isDebugEnabled()) {
                LOG.debug(getDebugLog(paramValues.get(i)));
            }

            sprocResult = null;
            try {
                sprocResult = executor.executeSProc(shardDs, getQuery(), paramValues.get(i), getTypes(), args,
                        returnType);
            } catch (final Exception e) {

                // remember all exceptions and go on
                exceptions.add("shardId: " + shardId + ", message: " + e.getMessage() + ", query: " + getQuery());
            }

            if (searchShards && sprocResult != null) {
                if (collectionResult) {
                    results.addAll((Collection) sprocResult);
                }

                break;
            }

            if (collectionResult && sprocResult != null) {
                results.addAll((Collection) sprocResult);
            }

            i++;
        }

        if (!exceptions.isEmpty()) {
            throw new IllegalStateException("Got exception(s) while executing sproc on shards: "
                    + Joiner.on(", ").join(exceptions));
        }

        return sprocResult;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Object executeInParallel(final DataSourceProvider dp, final Object[] args, final List<Integer> shardIds,
            final List<Object[]> paramValues, final Map<Integer, SameConnectionDatasource> transactionalDatasources,
            final List<?> results, Object sprocResult) {
        DataSource shardDs;
        final List<FutureTask<Object>> taskList = Lists.newArrayList();
        FutureTask<Object> task;
        int i = 0;

        for (final int shardId : shardIds) {
            shardDs = getShardDs(dp, transactionalDatasources, shardId);
            if (LOG.isDebugEnabled()) {
                LOG.debug(getDebugLog(paramValues.get(i)));
            }

            task = new FutureTask<Object>(new Call(this, shardDs, paramValues.get(i), args));
            taskList.add(task);
            parallelThreadPool.execute(task);
            i++;
        }

        final List<String> exceptions = Lists.newArrayList();
        for (final FutureTask<Object> taskToFinish : taskList) {
            try {
                sprocResult = taskToFinish.get();
            } catch (final InterruptedException ex) {

                // remember all exceptions and go on
                exceptions.add("got sharding execution exception: " + ex.getMessage() + ", query: " + getQuery());
            } catch (final ExecutionException ex) {

                // remember all exceptions and go on
                exceptions.add("got sharding execution exception: " + ex.getCause().getMessage() + ", query: "
                        + getQuery());
            }

            if (searchShards && sprocResult != null) {
                if (collectionResult) {
                    results.addAll((Collection) sprocResult);
                }

                break;
            }

            if (collectionResult) {
                results.addAll((Collection) sprocResult);
            }
        }

        if (!exceptions.isEmpty()) {
            throw new IllegalStateException("Got exception(s) while executing sproc on shards: "
                    + Joiner.on(", ").join(exceptions));
        }

        return sprocResult;
    }

    private DataSource getShardDs(final DataSourceProvider dp,
            final Map<Integer, SameConnectionDatasource> transactionIds, final int shardId) {
        if (transactionIds.isEmpty()) {
            return dp.getDataSource(shardId);
        }

        return transactionIds.get(shardId);
    }

    private Map<Integer, SameConnectionDatasource> startTransaction(final DataSourceProvider dp,
            final List<Integer> shardIds) throws SQLException {
        final Map<Integer, SameConnectionDatasource> ret = Maps.newHashMap();

        if (readOnly == false && writeTransaction != WriteTransaction.NONE) {
            for (final int shardId : shardIds) {
                final DataSource shardDs = dp.getDataSource(shardId);

                // we need to pin the calls to a single connection
                final SameConnectionDatasource sameConnDs = new SameConnectionDatasource(shardDs.getConnection());
                ret.put(shardId, sameConnDs);

                LOG.trace("startTransaction on shard [{}]", shardId);

                final Statement st = sameConnDs.getConnection().createStatement();
                st.execute("BEGIN");
                st.close();
            }
        }

        return ret;
    }

    private void commitTransaction(final Map<Integer, SameConnectionDatasource> transactionIds) {
        if (readOnly == false && writeTransaction != WriteTransaction.NONE) {
            if (writeTransaction == WriteTransaction.ONE_PHASE) {
                for (final Entry<Integer, SameConnectionDatasource> shardEntry : transactionIds.entrySet()) {
                    try {
                        LOG.trace("commitTransaction on shard [{}]", shardEntry.getKey());

                        final DataSource shardDs = shardEntry.getValue();
                        final Statement st = shardDs.getConnection().createStatement();
                        st.execute("COMMIT");
                        st.close();

                        shardEntry.getValue().close();
                    } catch (final Exception e) {

                        // do our best. we cannot rollback at this point.
                        // store other shards as much as possible.
                        LOG.error(
                            "ERROR: could not commitTransaction on shard [{}] - this will produce inconsistent data.",
                            shardEntry.getKey(), e);
                    }
                }
            } else if (writeTransaction == WriteTransaction.TWO_PHASE) {
                final Map<Integer, String> transactionIdMap = Maps.newHashMap();
                boolean commitFailed = false;
                for (final Entry<Integer, SameConnectionDatasource> shardEntry : transactionIds.entrySet()) {
                    try {
                        LOG.trace("prepare transaction on shard [{}]", shardEntry.getKey());

                        final DataSource shardDs = shardEntry.getValue();
                        final Statement st = shardDs.getConnection().createStatement();
                        final String transactionId = "'" + UUID.randomUUID() + "'";

                        transactionIdMap.put(shardEntry.getKey(), transactionId);

                        st.execute("PREPARE TRANSACTION " + transactionId);
                        st.close();
                    } catch (final Exception e) {
                        commitFailed = true;

                        // log, but go on, prepare other transactions - but they will be removed as well.
                        LOG.debug("prepare transaction [{}] on shard [{}] failed!",
                            new Object[] {transactionIdMap.get(shardEntry.getKey()), shardEntry.getKey(), e});
                    }
                }

                if (commitFailed) {
                    rollbackPrepared(transactionIds, transactionIdMap);
                } else {
                    for (final Entry<Integer, SameConnectionDatasource> shardEntry : transactionIds.entrySet()) {
                        try {
                            LOG.trace("commit prepared transaction [{}] on shard [{}]",
                                transactionIdMap.get(shardEntry.getKey()), shardEntry.getKey());

                            final DataSource shardDs = shardEntry.getValue();
                            final Statement st = shardDs.getConnection().createStatement();
                            st.execute("COMMIT PREPARED " + transactionIdMap.get(shardEntry.getKey()));
                            st.close();

                            shardEntry.getValue().close();
                        } catch (final Exception e) {
                            commitFailed = true;

                            // do our best. we cannot rollback at this point.
                            // store other shards as much as possible.
                            // the not yet stored transactions are visible in postgres prepared transactions
                            // a nagios check should detect them so that we can handle any errors
                            // that may be produced at this point.
                            LOG.error(
                                "FAILED: could not commit prepared transaction [{}] on shard [{}] - this will produce inconsistent data.",
                                new Object[] {transactionIdMap.get(shardEntry.getKey()), shardEntry.getKey(), e});
                        }
                    }

                    // for all failed commits:
                    if (commitFailed) {
                        rollbackPrepared(transactionIds, transactionIdMap);
                    }
                }
            } else {
                throw new IllegalArgumentException("Unknown writeTransaction state: " + writeTransaction);
            }
        }
    }

    private void rollbackPrepared(final Map<Integer, SameConnectionDatasource> transactionIds,
            final Map<Integer, String> transactionIdMap) {
        for (final Entry<Integer, SameConnectionDatasource> shardEntry : transactionIds.entrySet()) {
            try {
                LOG.error("rollback prepared transaction [{}] on shard [{}]", transactionIdMap.get(shardEntry.getKey()),
                    shardEntry.getKey());

                final DataSource shardDs = shardEntry.getValue();
                final Statement st = shardDs.getConnection().createStatement();
                st.execute("ROLLBACK PREPARED " + transactionIdMap.get(shardEntry.getKey()));
                st.close();

                shardEntry.getValue().close();
            } catch (final Exception e) {
                LOG.error(
                    "FAILED: could not rollback prepared transaction [{}] on shard [{}] - this will produce inconsistent data.",
                    new Object[] {transactionIdMap.get(shardEntry.getKey()), shardEntry.getKey(), e});
            }
        }
    }

    private void rollbackTransaction(final Map<Integer, SameConnectionDatasource> transactionIds) {
        if (readOnly == false && writeTransaction != WriteTransaction.NONE) {
            for (final Entry<Integer, SameConnectionDatasource> shardEntry : transactionIds.entrySet()) {
                try {
                    LOG.trace("rollbackTransaction on shard [{}]", shardEntry.getKey());

                    final DataSource shardDs = shardEntry.getValue();
                    final Statement st = shardDs.getConnection().createStatement();
                    st.execute("ROLLBACK");
                    st.close();

                    shardEntry.getValue().close();
                } catch (final Exception e) {
                    LOG.error("ERROR: could not rollback on shard [{}] - this will produce inconsistent data.",
                        shardEntry.getKey());
                }
            }
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(name);
        sb.append('(');

        boolean f = true;
        for (final StoredProcedureParameter p : params) {
            if (!f) {
                sb.append(',');
            }

            f = false;
            sb.append(p.getType());
            if (!"".equals(p.getTypeName())) {
                sb.append("=>").append(p.getTypeName());
            }
        }

        sb.append(')');
        return sb.toString();
    }
}
