package org.zalando.typemapper.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


import org.zalando.typemapper.core.ValueTransformer;
import org.zalando.typemapper.core.fieldMapper.AnyTransformer;
import org.zalando.typemapper.core.fieldMapper.DefaultObjectMapper;
import org.zalando.typemapper.core.fieldMapper.ObjectMapper;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DatabaseField {

    /**
     * Define the name of the database field.
     */
    String name() default "";

    /**
     * Define the position of the database field (is needed for passing field position when serializing object as
     * PostgreSQL ROW).
     */
    int position() default -1;

    /**
     * Transformer class of type {@link ValueTransformer} that can be used to transform
     * incoming value into a needed class (is needed for cases like non-standard way of passing enums).
     */
    Class<? extends ValueTransformer<?, ?>> transformer() default AnyTransformer.class;

    Class<? extends ObjectMapper<?>> mapper() default DefaultObjectMapper.class;

}
