package net.nemerosa.ontrack.extension.github.ingestion.processing.config

import net.nemerosa.ontrack.extension.github.ingestion.processing.model.normalizeName
import net.nemerosa.ontrack.extension.github.ingestion.support.FilterHelper

/**
 *
 */
private fun IngestionConfig.mustUseValidationJobPrefix(
    job: String,
    stepConfig: StepConfig?,
): Boolean =
    stepConfig?.validationJobPrefix // Trying at step config level
        ?: findJobConfig(job)?.validationJobPrefix // Trying at job level
        ?: general.validationJobPrefix // Trying at general level
        ?: TODO("Using the settings") // Using the settings

/**
 * Mapping of a step into a validation stamp name.
 *
 * @receiver The ingestion configuration
 * @param job The exact name of the job in the workflow
 * @param step The exact name of the step in the workflow (null when dealing with the job level)
 * @return Name of the validation stamp to use
 */
fun IngestionConfig.getValidationStampName(job: String, step: String?): String =
    if (step == null) {
        val jobConfig = findJobConfig(job)
        val baseName = jobConfig?.validation ?: job
        normalizeName(baseName)
    } else {
        val stepConfig = findStepConfig(step)
        val stepValidationJobPrefix = mustUseValidationJobPrefix(job, stepConfig)
        val baseName = if (stepConfig?.validation != null) {
            if (stepValidationJobPrefix) {
                val jobName = getValidationStampName(job, null)
                "$jobName-${stepConfig.validation}"
            } else {
                stepConfig.validation
            }
        } else {
            if (stepValidationJobPrefix) {
                val jobName = getValidationStampName(job, null)
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
fun IngestionConfig.findStepConfig(step: String): StepConfig? =
    steps.find { it.name == step }

/**
 * Getting (if any) the configuration for a specific job using its exact name
 *
 * @receiver The ingestion configuration
 * @param job Exact name of the job
 * @return Job configuration or null if not configured
 */
fun IngestionConfig.findJobConfig(job: String): JobConfig? =
    jobs.find { it.name == job }

/**
 * Checking if a job must be included.
 *
 * @receiver The ingestion configuration
 * @param job Exact name of the job
 * @return True if the job must be processed
 */
fun IngestionConfig.filterJob(job: String): Boolean =
    jobsFilter.includes(job)

/**
 * Checking if a step must be included.
 *
 * @receiver The ingestion configuration
 * @param step Exact name of the step
 * @return True if the step must be processed
 */
fun IngestionConfig.filterStep(step: String): Boolean =
    stepsFilter.includes(step)

/**
 * Inclusion filter based on the [FilterConfig].
 *
 * @receiver The filter config
 * @param name The value to filter
 * @receiver True if the [name] must be included
 */
fun FilterConfig.includes(name: String) =
    FilterHelper.includes(name, includes, excludes)
