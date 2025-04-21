package net.nemerosa.ontrack.it

import org.junit.jupiter.api.extension.ExtendWith

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ExtendWith(AsAdminTestExtension::class)
annotation class AsAdminTest
