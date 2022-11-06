package net.nemerosa.ontrack.extension.github.ingestion.config.model

import net.nemerosa.ontrack.extension.github.ingestion.config.model.support.FilterConfig
import net.nemerosa.ontrack.model.annotations.APIDescription

/**
 * Configuration for the ingestion of the steps
 *
 * @property filter Filter on the steps names
 */
@APIDescription("Configuration for the ingestion of the steps")
data class IngestionConfigSteps(
    @APIDescription("Filter on the steps names")
    val filter: FilterConfig = FilterConfig.none,
    @APIDescription("Mapping between step names and validation stamps")
    val mappings: List<StepIngestionConfigValidation> = emptyList(),
)