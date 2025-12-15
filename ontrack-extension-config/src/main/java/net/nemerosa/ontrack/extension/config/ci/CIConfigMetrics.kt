package net.nemerosa.ontrack.extension.config.ci

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APIName
import net.nemerosa.ontrack.model.metrics.MetricsDocumentation
import net.nemerosa.ontrack.model.metrics.MetricsMeterDocumentation
import net.nemerosa.ontrack.model.metrics.MetricsMeterType

/**
 * List of metrics for the CI configuration.
 */
@Suppress("ConstPropertyName")
@MetricsDocumentation
@APIName("CI config metrics")
@APIDescription("Metrics for the CI configuration")
object CIConfigMetrics {

    @APIDescription("Duration of the CI configuration for a project.")
    @MetricsMeterDocumentation(
        type = MetricsMeterType.TIMER
    )
    const val ciConfigProjectDuration = "ontrack_extension_config_project_duration"

    @APIDescription("Number of the CI configurations for projects.")
    @MetricsMeterDocumentation(
        type = MetricsMeterType.COUNT
    )
    const val ciConfigProjectCount = "ontrack_extension_config_project_count"

    @APIDescription("Duration of the CI configuration for a branch.")
    @MetricsMeterDocumentation(
        type = MetricsMeterType.TIMER
    )
    const val ciConfigBranchDuration = "ontrack_extension_config_branch_duration"

    @APIDescription("Number of the CI configurations for branches.")
    @MetricsMeterDocumentation(
        type = MetricsMeterType.COUNT
    )
    const val ciConfigBranchCount = "ontrack_extension_config_branch_count"

    @APIDescription("Duration of the CI configuration for a build.")
    @MetricsMeterDocumentation(
        type = MetricsMeterType.TIMER
    )
    const val ciConfigBuildDuration = "ontrack_extension_config_build_duration"

    @APIDescription("Number of the CI configurations for builds.")
    @MetricsMeterDocumentation(
        type = MetricsMeterType.COUNT
    )
    const val ciConfigBuildCount = "ontrack_extension_config_build_count"

}