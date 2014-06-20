package de.zalando.sprocwrapper.validation;

import java.lang.annotation.ElementType;
import java.lang.reflect.Method;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;

import com.google.common.base.Objects;

public class MethodConstraintValidationHolder<T> implements ConstraintViolation<T> {

    private final String message;
    private final String messageTemplate;
    private final T rootBean;
    private final Class<T> rootBeanClass;
    private final Object leafBean;
    private final Path propertyPath;
    private final Object invalidValue;
    private final ConstraintDescriptor<?> constraintDescriptor;
    private final ElementType elementType;
    private final Method method;
    private final Integer parameterIndex;
    private final String parameterName;

    public MethodConstraintValidationHolder(final String message, final String messageTemplate, final T rootBean,
            final Object leafBean, final Path propertyPath, final Object invalidValue,
            final ConstraintDescriptor<?> constraintDescriptor, final ElementType elementType, final Method method,
            final Integer parameterIndex, final String parameterName) {
        this.message = message;
        this.messageTemplate = messageTemplate;
        this.rootBean = rootBean;
        this.rootBeanClass = (Class<T>) rootBean.getClass();
        this.leafBean = leafBean;
        this.propertyPath = propertyPath;
        this.invalidValue = invalidValue;
        this.constraintDescriptor = constraintDescriptor;
        this.elementType = elementType;
        this.method = method;
        this.parameterIndex = parameterIndex;
        this.parameterName = parameterName;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getMessageTemplate() {
        return messageTemplate;
    }

    @Override
    public T getRootBean() {
        return rootBean;
    }

    @Override
    public Class<T> getRootBeanClass() {
        return rootBeanClass;
    }

    @Override
    public Object getLeafBean() {
        return leafBean;
    }

    @Override
    public Path getPropertyPath() {
        return propertyPath;
    }

    @Override
    public Object getInvalidValue() {
        return invalidValue;
    }

    @Override
    public ConstraintDescriptor<?> getConstraintDescriptor() {
        return constraintDescriptor;
    }

    public Method getMethod() {
        return method;
    }

    public Integer getParameterIndex() {
        return parameterIndex;
    }

    public String getParameterName() {
        return parameterName;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(method, parameterIndex, parameterName);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        MethodConstraintValidationHolder<?> other = (MethodConstraintValidationHolder<?>) obj;

        return Objects.equal(method, other.method) && Objects.equal(parameterIndex, other.parameterIndex)
                && Objects.equal(parameterName, other.parameterName);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MethodConstraintValidationHolder [message=");
        builder.append(message);
        builder.append(", messageTemplate=");
        builder.append(messageTemplate);
        builder.append(", rootBean=");
        builder.append(rootBean);
        builder.append(", rootBeanClass=");
        builder.append(rootBeanClass);
        builder.append(", leafBean=");
        builder.append(leafBean);
        builder.append(", propertyPath=");
        builder.append(propertyPath);
        builder.append(", invalidValue=");
        builder.append(invalidValue);
        builder.append(", constraintDescriptor=");
        builder.append(constraintDescriptor);
        builder.append(", elementType=");
        builder.append(elementType);
        builder.append(", method=");
        builder.append(method);
        builder.append(", parameterIndex=");
        builder.append(parameterIndex);
        builder.append(", parameterName=");
        builder.append(parameterName);
        builder.append("]");
        return builder.toString();
    }

}
