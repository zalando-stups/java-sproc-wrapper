package de.zalando.sprocwrapper.proxy.executors;

import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import de.zalando.typemapper.core.ValueTransformer;
import de.zalando.typemapper.core.fieldMapper.GlobalValueTransformerRegistry;

/**
 * This Executor wraps stored procedure calls that use advisory locks and / or need different statement timeouts set.
 *
 * @author  jmussler
 */
public class GlobalTransformerExecutorWrapper implements Executor {

    private final Executor originalExecutor;

    private static final Logger LOG = LoggerFactory.getLogger(GlobalTransformerExecutorWrapper.class);

    public GlobalTransformerExecutorWrapper(final Executor e) {
        originalExecutor = e;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Object executeSProc(final DataSource ds, final String sql, final Object[] args, final int[] types,
            final Object[] originalArgs, final Class returnType) {
        final Object result = originalExecutor.executeSProc(ds, sql, args, types, originalArgs, String.class);
        if (result == null) {
            return null;
        }

        final ValueTransformer<?, ?> valueTransformerForClass = GlobalValueTransformerRegistry
                .getValueTransformerForClass(returnType);
        if (valueTransformerForClass != null) {
            if (Collection.class.isAssignableFrom(result.getClass())) {
                final List ret = Lists.newArrayList();
                for (final Object o : (Collection<?>) result) {
                    ret.add(valueTransformerForClass.unmarshalFromDb((String) o));
                }

                return ret;
            } else {
                return valueTransformerForClass.unmarshalFromDb((String) result);
            }
        } else {
            LOG.error(
                "Could not find a global value transformer for type [{}]. RETURNING NULL instead of transformed [{}] value.",
                returnType, result);
        }

        return null;
    }
}
