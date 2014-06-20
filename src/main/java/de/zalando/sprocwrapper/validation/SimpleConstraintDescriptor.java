package de.zalando.sprocwrapper.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.ValidationException;
import javax.validation.metadata.ConstraintDescriptor;

public class SimpleConstraintDescriptor<T extends Annotation> implements ConstraintDescriptor<T> {

    private static final String PAYLOAD = "payload";

    private T annotation;
    private Set<Class<?>> groups;
    private Set<Class<? extends Payload>> payload;
    private List<Class<? extends ConstraintValidator<T, ?>>> constraintValidatorClasses;
    private Map<String, Object> attributes;
    private Set<ConstraintDescriptor<?>> composingConstraints;
    private boolean reportAsSingleViolation;

    public SimpleConstraintDescriptor(final T annotation, final Set<Class<?>> groups,
            final List<Class<? extends ConstraintValidator<T, ?>>> constraintValidatorClasses,
            final Set<ConstraintDescriptor<?>> composingConstraints) {
        this.annotation = annotation;
        this.groups = groups;
        this.payload = buildPayloadSet(annotation);
        this.constraintValidatorClasses = constraintValidatorClasses;
        this.attributes = buildAnnotationParameterMap(annotation);
        this.composingConstraints = composingConstraints;
        this.reportAsSingleViolation = ((Class<T>) this.annotation.annotationType()).isAnnotationPresent(
                ReportAsSingleViolation.class);
    }

    @Override
    public T getAnnotation() {
        return annotation;
    }

    @Override
    public Set<Class<?>> getGroups() {
        return groups;
    }

    @Override
    public Set<Class<? extends Payload>> getPayload() {
        return payload;
    }

    @Override
    public List<Class<? extends ConstraintValidator<T, ?>>> getConstraintValidatorClasses() {
        return constraintValidatorClasses;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Set<ConstraintDescriptor<?>> getComposingConstraints() {
        return composingConstraints;
    }

    @Override
    public boolean isReportAsSingleViolation() {
        return reportAsSingleViolation;
    }

    private Map<String, Object> buildAnnotationParameterMap(final Annotation annotation) {
        final Method[] declaredMethods = annotation.annotationType().getDeclaredMethods();
        Map<String, Object> parameters = new HashMap<String, Object>(declaredMethods.length);
        for (Method m : declaredMethods) {
            try {
                parameters.put(m.getName(), m.invoke(annotation));
            } catch (IllegalAccessException e) {
                throw new ValidationException("Unable to read annotation attributes: " + annotation.getClass(), e);
            } catch (InvocationTargetException e) {
                throw new ValidationException("Unable to read annotation attributes: " + annotation.getClass(), e);
            }
        }

        return Collections.unmodifiableMap(parameters);
    }

    private Set<Class<? extends Payload>> buildPayloadSet(final T annotation) {
        Set<Class<? extends Payload>> payloadSet = new HashSet<Class<? extends Payload>>();
        Class<Payload>[] payloadFromAnnotation;
        try {
            payloadFromAnnotation = getAnnotationParameter(annotation, PAYLOAD, Class[].class);
        } catch (ValidationException e) {
            payloadFromAnnotation = null;
        }

        if (payloadFromAnnotation != null) {
            payloadSet.addAll(Arrays.asList(payloadFromAnnotation));
        }

        return Collections.unmodifiableSet(payloadSet);
    }

    private <P> P getAnnotationParameter(final Annotation annotation, final String parameterName, final Class<P> type) {

        try {
            Method m = annotation.getClass().getMethod(parameterName);
            m.setAccessible(true);

            Object o = m.invoke(annotation);
            if (o.getClass().getName().equals(type.getName())) {
                return (P) o;
            } else {
                String msg = "Wrong parameter type. Expected: " + type.getName() + " Actual: " + o
                        .getClass().getName();
                throw new ValidationException(msg);
            }
        } catch (NoSuchMethodException e) {
            String msg = "The specified annotation defines no parameter '" + parameterName + "'.";
            throw new ValidationException(msg, e);
        } catch (IllegalAccessException e) {
            String msg = "Unable to get '" + parameterName + "' from " + annotation.getClass().getName();
            throw new ValidationException(msg, e);
        } catch (InvocationTargetException e) {
            String msg = "Unable to get '" + parameterName + "' from " + annotation.getClass().getName();
            throw new ValidationException(msg, e);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SimpleConstraintDescriptor [annotation=");
        builder.append(annotation);
        builder.append(", groups=");
        builder.append(groups);
        builder.append(", payload=");
        builder.append(payload);
        builder.append(", constraintValidatorClasses=");
        builder.append(constraintValidatorClasses);
        builder.append(", attributes=");
        builder.append(attributes);
        builder.append(", composingConstraints=");
        builder.append(composingConstraints);
        builder.append(", reportAsSingleViolation=");
        builder.append(reportAsSingleViolation);
        builder.append("]");
        return builder.toString();
    }

}
