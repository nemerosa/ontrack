package net.nemerosa.ontrack.job.support

import net.nemerosa.ontrack.common.api.APIDescription
import net.nemerosa.ontrack.common.api.APIName
import net.nemerosa.ontrack.common.doc.MetricsDocumentation
import net.nemerosa.ontrack.common.doc.MetricsMeterDocumentation
import net.nemerosa.ontrack.common.doc.MetricsMeterType

@Suppress("ConstPropertyName")
@MetricsDocumentation
@APIName("CI config metrics")
@APIDescription("Metrics for the CI configuration")
object JobMetrics {

    @APIDescription("Total number of registered background jobs.")
    @MetricsMeterDocumentation(
        type = MetricsMeterType.COUNT
    )
    const val ontrack_job_count_total = "ontrack_job_count_total"

    @APIDescription("Total number of running background jobs.")
    @MetricsMeterDocumentation(
        type = MetricsMeterType.COUNT
    )
    const val ontrack_job_running_total = "ontrack_job_running_total"

    @APIDescription("Total number of disabled background jobs.")
    @MetricsMeterDocumentation(
        type = MetricsMeterType.COUNT
    )
    const val ontrack_job_disabled_total = "ontrack_job_disabled_total"

    @APIDescription("Total number of paused background jobs.")
    @MetricsMeterDocumentation(
        type = MetricsMeterType.COUNT
    )
    const val ontrack_job_paused_total = "ontrack_job_paused_total"

    @APIDescription("Total number of invalid background jobs.")
    @MetricsMeterDocumentation(
        type = MetricsMeterType.COUNT
    )
    const val ontrack_job_invalid_total = "ontrack_job_invalid_total"

    @APIDescription("Total number of errored background jobs.")
    @MetricsMeterDocumentation(
        type = MetricsMeterType.COUNT
    )
    const val ontrack_job_error_total = "ontrack_job_error_total"

    @APIDescription("Total number of timed out background jobs.")
    @MetricsMeterDocumentation(
        type = MetricsMeterType.COUNT
    )
    const val ontrack_job_timeout_total = "ontrack_job_timeout_total"

    @APIDescription("Duration of the last job execution")
    @MetricsMeterDocumentation(
        type = MetricsMeterType.TIMER,
    )
    const val ontrack_job_duration = "ontrack_job_duration"

    const val tag_job_category = "job_category"
    const val tag_job_type = "job_type"
    const val tag_job_key = "job_key"
}