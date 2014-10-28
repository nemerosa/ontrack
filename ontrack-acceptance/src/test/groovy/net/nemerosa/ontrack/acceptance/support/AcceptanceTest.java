package net.nemerosa.ontrack.acceptance.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AcceptanceTest {

    /**
     * Labels to exclude
     */
    String[] excludes() default {};

}