package de.zalando.sprocwrapper.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NotNull;

public class NotNullValidator implements ConstraintValidator<NotNull, Object> {

    public void initialize(final NotNull parameters) { }

    public boolean isValid(final Object object, final ConstraintValidatorContext constraintValidatorContext) {
        return object != null;
    }

}
