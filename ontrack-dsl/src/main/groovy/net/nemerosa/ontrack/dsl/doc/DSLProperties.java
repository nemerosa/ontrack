package net.nemerosa.ontrack.dsl.doc;

import java.lang.annotation.*;

/**
 * Designates a class which is used as the properties or configuration class for an outer
 * class.
 * The description of the annotated class will be included as `properties` into the description of the outer class,
 * as "Properties", with an additional level of indentation.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DSLProperties {
}
