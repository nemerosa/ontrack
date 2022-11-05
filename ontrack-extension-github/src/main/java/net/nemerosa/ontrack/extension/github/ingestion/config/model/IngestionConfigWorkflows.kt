package net.nemerosa.ontrack.extension.github.ingestion.config.model

import net.nemerosa.ontrack.extension.github.ingestion.config.model.support.FilterConfig
import net.nemerosa.ontrack.model.annotations.APIDescription

/**
 * Configuration for the ingestion of the workflows
 *
 * @property filter Filter on the workflow names
 */
@APIDescription("Configuration for the ingestion of the workflows")
data class IngestionConfigWorkflows(
    @APIDescription("Filter on the workflow names")
    val filter: FilterConfig = FilterConfig.all,
    @APIDescription("Using the workflow name as a prefix for the validation stamps")
    val validationPrefix: Boolean = true,
    @APIDescription("Mapping between workflow names and validation stamps")
    val mappings: List<IngestionConfigValidation> = emptyList(),
)