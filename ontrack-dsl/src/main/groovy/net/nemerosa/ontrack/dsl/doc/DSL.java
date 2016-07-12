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

}
