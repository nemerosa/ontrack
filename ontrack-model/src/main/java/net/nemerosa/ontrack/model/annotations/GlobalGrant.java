package net.nemerosa.ontrack.model.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GlobalGrant {

    Class<? extends GlobalFunction> value();

}
