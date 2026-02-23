package net.nemerosa.ontrack.extension.scm.search

import net.nemerosa.ontrack.common.api.APIDescription
import net.nemerosa.ontrack.common.api.APIName
import net.nemerosa.ontrack.common.doc.MetricsDocumentation
import net.nemerosa.ontrack.common.doc.MetricsMeterDocumentation
import net.nemerosa.ontrack.common.doc.MetricsMeterTag
import net.nemerosa.ontrack.common.doc.MetricsMeterType

@Suppress("ConstPropertyName")
@MetricsDocumentation
@APIName("SCM search index metrics")
@APIDescription("Metrics for the indexation of SCM commits and issues")
object ScmSearchIndexMetrics {

    const val TAG_PROJECT = "project"

    @APIDescription("Duration of an indexation run for a project.")
    @MetricsMeterDocumentation(
        type = MetricsMeterType.TIMER,
        tags = [
            MetricsMeterTag(TAG_PROJECT, "Name of the project")
        ]
    )
    const val scmSearchIndexIndexationTime = "ontrack_extension_scm_search_index_indexation_time"

}