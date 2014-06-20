package de.zalando.sprocwrapper.proxy.executors;

import java.lang.annotation.ElementType;

import java.util.Set;

import javax.sql.DataSource;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;
import javax.validation.metadata.ConstraintDescriptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.Parameter;

import de.zalando.sprocwrapper.proxy.InvocationContext;
import de.zalando.sprocwrapper.validation.MethodConstraintValidationHolder;
import de.zalando.sprocwrapper.validation.NotNullValidator;
import de.zalando.sprocwrapper.validation.SimpleConstraintDescriptor;
import de.zalando.sprocwrapper.validation.SimplePath;

/**
 * This Executor wraps stored procedure calls that use advisory locks and / or need different statement timeouts set.
 *
 * @author  carsten.wolters
 */
public class ValidationExecutorWrapper implements Executor {

    private static final ConstraintValidator<NotNull, Object> NOT_NULL_VALIDATOR = new NotNullValidator();

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
                validateParameters(invocationContext, validator, invocationContext.getArgs(), constraintViolations);
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

    private void validateParameters(final InvocationContext invocationContext, final Validator validator,
            final Object[] originalArgs, final Set<ConstraintViolation<?>> constraintViolations) {

        if (originalArgs != null) {
            Invokable<?, Object> invokable = Invokable.from(invocationContext.getMethod());
            for (int i = 0; i < originalArgs.length; i++) {
                Object arg = originalArgs[i];
                if (!NOT_NULL_VALIDATOR.isValid(arg, null)) {

                    // JSR 303 doesn't support method level validation
                    // we should provide at least a dummy implementation to detect @NotNull annotations
                    // we should migrate our implementation to bean validation 1.1 when possible
                    final Parameter parameter = invokable.getParameters().get(i);
                    final NotNull annotation = parameter.getAnnotation(NotNull.class);
                    if (annotation != null) {

                        final String parameterName = "arg" + i;
                        final ConstraintDescriptor<NotNull> descriptor = new SimpleConstraintDescriptor<NotNull>(
                                annotation, ImmutableSet.<Class<?>>of(Default.class),
                                ImmutableList.<Class<? extends ConstraintValidator<NotNull, ?>>>of(
                                    NotNullValidator.class), null);

                        final ConstraintViolation<Object> violation = new MethodConstraintValidationHolder<Object>(

                                // message
                                "may not be null",

                                // messageTemplate
                                annotation.message(),

                                // rootBean
                                invocationContext.getProxy(),

                                // leafBean (the object the method is executed on)
                                invocationContext.getProxy(),

                                // propertyPath
                                SimplePath.createPathForMethodParameter(invocationContext.getMethod(), parameterName),

                                // invalidValue
                                null,

                                // constraintDescriptor
                                descriptor,

                                // elementType
                                ElementType.PARAMETER,

                                // method
                                invocationContext.getMethod(),

                                // parameterIndex
                                i,

                                // parameterName
                                parameterName);
                        constraintViolations.add(violation);
                    }
                } else {
                    constraintViolations.addAll(validator.validate(arg));
                }
            }
        }
    }
}
