package net.nemerosa.ontrack.extension.github.ingestion.config.parser.old

import com.fasterxml.jackson.annotation.JsonProperty
import net.nemerosa.ontrack.extension.github.ingestion.config.model.*
import net.nemerosa.ontrack.extension.github.ingestion.config.model.IngestionConfig.Companion.V1_VERSION
import net.nemerosa.ontrack.extension.github.ingestion.config.model.tagging.IngestionTaggingConfig

/**
 * Configuration for the ingestion in V1.
 */
@Deprecated("Use the V2 model")
data class IngestionV1Config(
    val version: String = V1_VERSION,
    val workflows: IngestionConfigWorkflows = IngestionConfigWorkflows(),
    val jobs: IngestionConfigJobs = IngestionConfigJobs(),
    val steps: IngestionConfigSteps = IngestionConfigSteps(),
    val setup: IngestionConfigSetup = IngestionConfigSetup(),
    val tagging: IngestionTaggingConfig = IngestionTaggingConfig(),
    /**
     * Added with a default legacy value
     */
    @JsonProperty("vs-name-normalization")
    val vsNameNormalization: IngestionConfigVSNameNormalization = IngestionConfigVSNameNormalization.LEGACY,
) {
    fun convert() = IngestionConfig(
        version = version,
        workflows = workflows,
        jobs = jobs,
        steps = steps,
        setup = setup,
        tagging = tagging,
        vsNameNormalization = vsNameNormalization,
    )
}