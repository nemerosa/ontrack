package net.nemerosa.ontrack.extension.github.ingestion.config.model

/**
 * Configuration between a job and a validation stamp
 */
class JobIngestionConfigValidation(
    name: String,
    validation: String? = null,
    description: String? = null,
) : AbstractIngestionConfigValidation(
    name = name,
    validation = validation,
    description = description
)