package net.nemerosa.ontrack.dsl.v4.doc;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DSL {

    /**
     * Element description
     *
     * @return Short description to be used for the element
     */
    String value() default "";

    /**
     * Optional ID for the element, used to disambiguate between several methods for example
     *
     * @return ID to be used for the element
     */
    String id() default "";

    /**
     * Count of arguments, to distinguish between several Java methods created from the same Groovy
     * method with parameters having default values. Defaults to -1, meaning that we do not want to
     * check.
     *
     * @return Count of parameters for the method
     */
    int count() default -1;

}
