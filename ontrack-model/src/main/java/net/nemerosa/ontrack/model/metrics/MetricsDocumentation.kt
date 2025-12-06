package net.nemerosa.ontrack.model.metrics

import org.springframework.stereotype.Component

/**
 * This annotation is used to annotate beans which act as documentation
 * for a list of metrics.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Component
annotation class MetricsDocumentation
