package net.nemerosa.ontrack.extension.github.ingestion.config.model

import net.nemerosa.ontrack.model.annotations.APIDescription

/**
 * Setup of Ontrack resources
 */
@APIDescription("Setup of Ontrack resources")
data class IngestionConfigSetup(
    @APIDescription("Configuration of the validation stamps")
    val validations: List<IngestionConfigCascValidation> = emptyList(),
    @APIDescription("Configuration of the promotion levels")
    val promotions: List<IngestionConfigCascPromotion> = emptyList(),
    @APIDescription("Casc for the project")
    val project: IngestionConfigCascSetup = IngestionConfigCascSetup(),
    @APIDescription("Casc for the branch")
    val branch: IngestionConfigCascSetup = IngestionConfigCascSetup(),
)