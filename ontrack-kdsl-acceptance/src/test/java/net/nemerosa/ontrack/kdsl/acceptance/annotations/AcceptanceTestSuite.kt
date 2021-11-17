package net.nemerosa.ontrack.kdsl.acceptance.annotations

import org.springframework.stereotype.Component

@Component
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class AcceptanceTestSuite
