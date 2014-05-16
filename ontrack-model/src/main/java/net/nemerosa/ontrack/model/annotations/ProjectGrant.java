package net.nemerosa.ontrack.model.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ProjectGrant {

    Class<? extends ProjectFunction> fn();

    String before() default "";

    String after() default "";

}
