package de.zalando.typemapper.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DatabaseType {

    /**
     * Define the name of the database type, that a class represents.
     */
    String name() default "";

    /**
     * Flattens hierarchical structure of object and makes parent fields exposed to PgTypeHelper.
     */
    boolean inheritance() default false;
}
