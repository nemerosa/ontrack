package net.nemerosa.ontrack.extension.github.ingestion.config.model

import net.nemerosa.ontrack.extension.github.ingestion.config.model.tagging.IngestionTaggingConfig
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.normalizeName
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

    /**
     * Gets the validation stamp name for a given job and step
     */
    fun getValidationStampName(job: String, step: String?): String =
        if (step == null) {
            val jobConfig = findJobValidationConfig(job)
            val baseName = jobConfig?.validation ?: job
            normalizeName(baseName)
        } else {
            val stepConfig = findStepValidationConfig(step)
            // Step contribution
            val stepValidation = stepConfig?.validation ?: step
            // Job contribution
            val jobContribution = if (stepConfig?.validationPrefix != false) {
                val jobConfig = findJobValidationConfig(job)
                val jobValidation = jobConfig?.validation ?: job
                "$jobValidation-$stepValidation"
            } else {
                stepValidation
            }
            // Normalization
            normalizeName(jobContribution)
        }

    /**
     * Gets the validation stamp description for a given job and step
     */
    fun getValidationStampDescription(job: String, step: String?): String =
        if (step != null) {
            val cfg = findStepValidationConfig(step)
            cfg?.description ?: step
        } else {
            val cfg = findJobValidationConfig(job)
            cfg?.description ?: job
        }

    private fun findStepValidationConfig(step: String): StepIngestionConfigValidation? =
        steps.mappings.find { it.name == step }

    private fun findJobValidationConfig(job: String): JobIngestionConfigValidation? =
        jobs.mappings.find { it.name == job }
}