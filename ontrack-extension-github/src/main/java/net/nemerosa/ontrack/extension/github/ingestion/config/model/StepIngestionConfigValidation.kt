package net.nemerosa.ontrack.extension.github.ingestion.config.model

import net.nemerosa.ontrack.model.annotations.APIDescription

/**
 * Configuration between a step and a validation stamp
 */
class StepIngestionConfigValidation(
    name: String,
    validation: String? = null,
    description: String? = null,
    @APIDescription("Must we use the job name as a prefix to the validation stamp?")
    val validationPrefix: Boolean? = null,
) : AbstractIngestionConfigValidation(
    name = name,
    validation = validation,
    description = description
)
