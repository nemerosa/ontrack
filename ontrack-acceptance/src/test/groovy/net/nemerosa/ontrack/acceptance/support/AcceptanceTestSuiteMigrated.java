package net.nemerosa.ontrack.acceptance.support;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker interface used only to mention that a given test
 * has been adapted to Next UI or the KDSL interface.
 */
@Component
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface AcceptanceTestSuiteMigrated {

}