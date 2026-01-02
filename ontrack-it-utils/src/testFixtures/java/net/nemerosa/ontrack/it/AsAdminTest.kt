package net.nemerosa.ontrack.it

import org.junit.jupiter.api.extension.ExtendWith

/**
 * When a test class or test function in annotated with this annotation,
 * the test execution is wrapped inside the security context of
 * an account with all rights (belonging to the built-in Administrators group).
 *
 * While it's perfectly OK to use this annotation to run tests, any test related
 * to permissions should avoid using it and should rely on explicit methods
 * to setup the security context.
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ExtendWith(AsAdminTestExtension::class)
annotation class AsAdminTest
