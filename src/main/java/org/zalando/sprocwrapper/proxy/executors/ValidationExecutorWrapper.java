package org.zalando.sprocwrapper.proxy.executors;

import com.google.common.collect.Sets;
import org.zalando.sprocwrapper.proxy.InvocationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

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
            LOG.error("Could not build default validator factory", e);
        }
    }

    public ValidationExecutorWrapper(final Executor e) {
        executor = e;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object executeSProc(final DataSource ds, final String sql, final Object[] args, final int[] types,
                               final InvocationContext invocationContext, final Class returnType) {

        if (factory != null) {
            final Validator validator = factory.getValidator();
            final Set<ConstraintViolation<?>> constraintViolations = Sets.newHashSet();

            if (args != null) {
                constraintViolations.addAll(validator.forExecutables().validateParameters(
                        invocationContext.getProxy(), invocationContext.getMethod(), invocationContext.getArgs()));
            }

            if (!constraintViolations.isEmpty()) {
                throw new ConstraintViolationException("SPROC call does not meet all constraints. Aborting.",
                    constraintViolations);
            }

            final Object result = executor.executeSProc(ds, sql, args, types, invocationContext, returnType);

            if (result != null) {
                constraintViolations.addAll(validator.validate(result));

                if (!constraintViolations.isEmpty()) {
                    throw new ConstraintViolationException("SPROC return object does not meet all constraints.",
                        constraintViolations);
                }
            }

            return result;
        }

        // else do nothing
        return executor.executeSProc(ds, sql, args, types, invocationContext, returnType);
    }

}
