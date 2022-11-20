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
    @APIDescription("Creation of validation runs at the workflow level")
    val validations: IngestionConfigWorkflowValidations = IngestionConfigWorkflowValidations(),
    @APIDescription("List of events to accept for the processing of a workflow")
    val events: List<String> = listOf("push"),
    @APIDescription("Filter on the Git branch names")
    val branchFilter: FilterConfig = FilterConfig.all,
    @APIDescription("Filtering the pull requests")
    val includePRs: Boolean = true,
    @APIDescription("Build identification strategy")
    val buildIdStrategy: IngestionConfigBuildIdStategy = IngestionConfigBuildIdStategy(),
)