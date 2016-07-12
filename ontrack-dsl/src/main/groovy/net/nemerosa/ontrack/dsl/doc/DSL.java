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

}
