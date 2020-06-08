package net.nemerosa.ontrack.model.annotations;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface APIDescription {
    /**
     * Value for the description
     */
    String value();
}
