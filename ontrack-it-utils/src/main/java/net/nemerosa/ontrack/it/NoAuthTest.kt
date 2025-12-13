package net.nemerosa.ontrack.it

import org.junit.jupiter.api.extension.ExtendWith

/**
 * Used to remove any authentication context for the execution of a test.
 *
 * Can use used in conjunction, for example, with a test class annotated
 * with [AsAdminTest] when one function should not have any authentication.
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ExtendWith(NoAuthTestExtension::class)
annotation class NoAuthTest
