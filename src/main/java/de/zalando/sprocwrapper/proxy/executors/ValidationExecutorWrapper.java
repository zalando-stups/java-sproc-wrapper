package de.zalando.sprocwrapper.proxy.executors;

import java.util.Set;

import javax.sql.DataSource;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * This Executor wraps stored procedure calls that use advisory locks and / or need different statement timeouts set.
 *
 * @author  carsten.wolters
 */
public class ValidationExecutorWrapper implements Executor {

    private final Executor executor;

    private static final Logger LOG = LoggerFactory.getLogger(ValidationExecutorWrapper.class);
    private static ValidatorFactory factory;

    static {
        try {
            factory = Validation.buildDefaultValidatorFactory();
        } catch (final Exception e) {
            // ignore - we may not be able to initialize the factory.
        }
    }

    public ValidationExecutorWrapper(final Executor e) {
        executor = e;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object executeSProc(final DataSource ds, final String sql, final Object[] args, final int[] types,
            final Object[] originalArgs, final Class returnType) {

        if (factory != null) {
            final Validator validator = factory.getValidator();
            final Set<ConstraintViolation<?>> constraintViolations = Sets.newHashSet();
            if (args != null) {
                for (final Object arg : originalArgs) {
                    constraintViolations.addAll(validator.validate(arg));
                }
            }

            if (!constraintViolations.isEmpty()) {
                LOG.error("SPROC call does not meet all constraints. Aborting [{}].", constraintViolations);
                throw new ConstraintViolationException("SPROC call does not meet all constraints. Aborting.",
                    constraintViolations);
            }

            final Object result = executor.executeSProc(ds, sql, args, types, originalArgs, returnType);

            if (result != null) {
                constraintViolations.addAll(validator.validate(result));

                if (!constraintViolations.isEmpty()) {
                    LOG.error("SPROC return object does not meet all constraints. Aborting [{}].",
                        constraintViolations);
                    throw new ConstraintViolationException("SPROC return object does not meet all constraints.",
                        constraintViolations);
                }
            }

            return result;
        }

        // else do nothing
        return executor.executeSProc(ds, sql, args, types, originalArgs, returnType);
    }
}
