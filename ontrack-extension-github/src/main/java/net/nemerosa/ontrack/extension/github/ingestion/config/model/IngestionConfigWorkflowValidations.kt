package net.nemerosa.ontrack.extension.github.ingestion.config.model

import net.nemerosa.ontrack.extension.github.ingestion.config.model.support.FilterConfig
import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("Creation of validation runs at the workflow level")
data class IngestionConfigWorkflowValidations(
    @APIDescription("Is the creation of validation runs for workflows enabled?")
    val enabled: Boolean = true,
    @APIDescription("Filter on workflows to select the ones for which a validation must be created")
    val filter: FilterConfig = FilterConfig.all,
    @APIDescription("Prefix to use for the validation stamp")
    val prefix: String = "workflow-",
    @APIDescription("Suffix to use for the validation stamp")
    val suffix: String = "",
)