package net.nemerosa.ontrack.dsl.doc;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DSLMethod {

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

    /**
     * Reference to include, as see &lt;&lt;method&gt;&gt;, in the same class.
     *
     * @return Name of the reference (ID of the method in the same class)
     */
    String see() default "";

}
