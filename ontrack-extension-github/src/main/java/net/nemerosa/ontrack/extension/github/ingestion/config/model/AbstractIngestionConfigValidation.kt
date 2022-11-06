package net.nemerosa.ontrack.extension.github.ingestion.config.model

import net.nemerosa.ontrack.model.annotations.APIDescription

/**
 * Configuration between a job or step and a validation stamp
 */
abstract class AbstractIngestionConfigValidation(
    @APIDescription("Exact name of the job/step in the workflow")
    val name: String,
    @APIDescription("Name of the validation stamp to use (instead of a generated one)")
    val validation: String? = null,
    @APIDescription("Description for the validation stamp")
    val description: String? = null,
)