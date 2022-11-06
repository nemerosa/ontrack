package net.nemerosa.ontrack.extension.github.ingestion.config.model

import net.nemerosa.ontrack.extension.github.ingestion.config.model.support.FilterConfig
import net.nemerosa.ontrack.model.annotations.APIDescription

/**
 * Configuration for the ingestion of the jobs
 */
@APIDescription("Configuration for the ingestion of the jobs")
data class IngestionConfigJobs(
    @APIDescription("Filter on the jobs names")
    val filter: FilterConfig = FilterConfig.all,
    @APIDescription("Using the job name as a prefix for the validation stamps")
    val validationPrefix: Boolean = true,
    @APIDescription("Mapping between job names and validation stamps")
    val mappings: List<JobIngestionConfigValidation> = emptyList(),
)