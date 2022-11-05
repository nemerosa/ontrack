package net.nemerosa.ontrack.extension.github.ingestion.config.model

import net.nemerosa.ontrack.extension.github.ingestion.config.model.tagging.IngestionTaggingConfig
import net.nemerosa.ontrack.model.annotations.APIDescription

/**
 * Configuration for the ingestion.
 *
 * @property workflows Configuration for the ingestion of the workflows
 * @property jobs Configuration for the ingestion of the jobs
 * @property steps Configuration for the ingestion of the steps
 * @property setup Setup of Ontrack resources
 * @property tagging Configuration for the tag ingestion
 */
data class IngestionConfig(
    @APIDescription("Version of the configuration")
    val version: String = V1_VERSION,
    @APIDescription("Configuration for the ingestion of the workflows")
    val workflows: IngestionConfigWorkflows = IngestionConfigWorkflows(),
    @APIDescription("Configuration for the ingestion of the jobs")
    val jobs: IngestionConfigJobs = IngestionConfigJobs(),
    @APIDescription("Configuration for the ingestion of the steps")
    val steps: IngestionConfigSteps = IngestionConfigSteps(),
    @APIDescription("Setup of Ontrack resources")
    val setup: IngestionConfigSetup = IngestionConfigSetup(),
    @APIDescription("Configuration for the tag ingestion")
    val tagging: IngestionTaggingConfig = IngestionTaggingConfig(),
) {
    companion object {
        const val V1_VERSION = "v1"
    }
}