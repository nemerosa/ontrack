package net.nemerosa.ontrack.extension.github.ingestion.processing.config

import net.nemerosa.ontrack.extension.github.ingestion.config.model.IngestionConfig
import net.nemerosa.ontrack.extension.github.ingestion.config.parser.old.OldJobConfig
import net.nemerosa.ontrack.extension.github.ingestion.config.parser.old.OldStepConfig
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.normalizeName
import net.nemerosa.ontrack.extension.github.ingestion.settings.GitHubIngestionSettings

/**
 *
 */
@Deprecated("Use the new model")
private fun IngestionConfig.mustUseValidationJobPrefix(
    job: String,
    stepConfig: OldStepConfig?,
    settings: GitHubIngestionSettings,
): Boolean = TODO()
//    stepConfig?.validationJobPrefix // Trying at step config level
//        ?: findJobConfig(job)?.validationJobPrefix // Trying at job level
//        ?: general.validationJobPrefix // Trying at general level
//        ?: settings.validationJobPrefix // Using the settings

/**
 * Mapping of a step into a validation stamp name.
 *
 * @receiver The ingestion configuration
 * @param settings The ingestion global settings
 * @param job The exact name of the job in the workflow
 * @param step The exact name of the step in the workflow (null when dealing with the job level)
 * @return Name of the validation stamp to use
 */
@Deprecated("Use the new model")
fun IngestionConfig.getValidationStampName(settings: GitHubIngestionSettings, job: String, step: String?): String =
    if (step == null) {
        val jobConfig = findJobConfig(job)
        val baseName = jobConfig?.validation ?: job
        normalizeName(baseName)
    } else {
        val stepConfig = findStepConfig(step)
        val stepValidationJobPrefix = mustUseValidationJobPrefix(job, stepConfig, settings)
        val baseName = if (stepConfig?.validation != null) {
            if (stepValidationJobPrefix) {
                val jobName = getValidationStampName(settings, job, null)
                "$jobName-${stepConfig.validation}"
            } else {
                stepConfig.validation
            }
        } else {
            if (stepValidationJobPrefix) {
                val jobName = getValidationStampName(settings, job, null)
                "$jobName-$step"
            } else {
                step
            }
        }
        // Normalization
        normalizeName(baseName)
    }

/**
 * Mapping of a step into a validation stamp description.
 *
 * @receiver The ingestion configuration
 * @param job The exact name of the job in the workflow
 * @param step The exact name of the step in the workflow (null when dealing with the job level)
 * @return Description of the validation stamp to use
 */
@Deprecated("Use the new model")
fun IngestionConfig.getValidationStampDescription(job: String, step: String?): String =
    if (step == null) {
        val jobConfig = findJobConfig(job)
        jobConfig?.description ?: job
    } else {
        val stepConfig = findStepConfig(step)
        stepConfig?.description ?: step
    }

/**
 * Getting (if any) the configuration for a specific step using its exact name
 *
 * @receiver The ingestion configuration
 * @param step Exact name of the step
 * @return Step configuration or null if not configured
 */
@Deprecated("Use the new model")
fun IngestionConfig.findStepConfig(step: String): OldStepConfig? = TODO()
// steps.find { it.name == step }

/**
 * Getting (if any) the configuration for a specific job using its exact name
 *
 * @receiver The ingestion configuration
 * @param job Exact name of the job
 * @return Job configuration or null if not configured
 */
@Deprecated("Use the new model")
fun IngestionConfig.findJobConfig(job: String): OldJobConfig? = TODO()
// jobs.find { it.name == job }

/**
 * Checking if a job must be included.
 *
 * @receiver The ingestion configuration
 * @param job Exact name of the job
 * @return True if the job must be processed
 */
@Deprecated("Use the new model")
fun IngestionConfig.filterJob(job: String): Boolean = TODO()
// jobsFilter.includes(job)

/**
 * Checking if a step must be included.
 *
 * @receiver The ingestion configuration
 * @param step Exact name of the step
 * @return True if the step must be processed
 */
@Deprecated("Use the new model")
fun IngestionConfig.filterStep(step: String): Boolean = TODO()
// stepsFilter.includes(step)

