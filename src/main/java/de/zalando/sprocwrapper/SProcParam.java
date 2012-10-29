package de.zalando.sprocwrapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author  jmussler
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface SProcParam {
    String name() default "";

    /**
     * database type name.
     *
     * @return
     */
    String type() default "";

    int sqlType() default -1;

    /**
     * whether the stored procedure argument contains sensitive information: sensitive parameters (e.g. "password") are
     * masked out in the debug log
     *
     * @return
     */
    boolean sensitive() default false;
}
