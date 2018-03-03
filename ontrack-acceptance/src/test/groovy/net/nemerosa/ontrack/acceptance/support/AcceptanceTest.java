package net.nemerosa.ontrack.acceptance.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AcceptanceTest {

    /**
     * Execution context
     */
    String[] value();

    /**
     * Only when the context is explicitly set?
     */
    boolean explicit() default false;

}