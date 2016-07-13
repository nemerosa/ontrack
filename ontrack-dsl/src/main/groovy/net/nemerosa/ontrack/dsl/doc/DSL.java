package net.nemerosa.ontrack.dsl.doc;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface DSL {

    /**
     * Element description
     */
    String description() default "";

    /**
     * Optional ID for the element, used to disambiguate between several methods for example
     */
    String id() default "";

    /**
     * Count of arguments, to distinguish between several Java methods created from the same Groovy
     * method with parameters having default values. Defaults to -1, meaning that we do not want to
     * check.
     */
    int count() default -1;

}
