package net.nemerosa.ontrack.extension.github.ingestion.processing.config

/**
 * Default path for the ingestion file
 */
const val INGESTION_CONFIG_FILE_PATH = ".github/ontrack/ingestion.yml"

/**
 * Configuration for the ingestion.
 *
 * @param general General settings
 * @param steps List of specific step configurations
 * @param jobs List of specific job configurations
 * @param jobsFilter Filtering on the jobs
 * @param stepsFilter Filtering on the steps
 */
data class IngestionConfig(
    val general: IngestionConfigGeneral = IngestionConfigGeneral(),
    val steps: List<StepConfig> = emptyList(),
    val jobs: List<JobConfig> = emptyList(),
    val jobsFilter: FilterConfig = FilterConfig(),
    val stepsFilter: FilterConfig = FilterConfig(),
)

/**
 * General settings
 *
 * @param skipJobs Must jobs be considered as validations?
 */
data class IngestionConfigGeneral(
    val skipJobs: Boolean = true,
)

/**
 * Filter rule
 *
 * @param includes Regular expression to include the items
 * @param excludes Regular expression to exclude the items (empty = no exclusion)
 */
data class FilterConfig(
    val includes: String = ".*",
    val excludes: String = "",
)

/**
 * Step configuration
 *
 * @param name Exact name of the step in the workflow
 * @param validation Name of the validation stamp to use (instead of a generated one)
 * @param validationJobPrefix Must we use the job name as a prefix to the validation stamp?
 * @param description Description for the validation stamp
 */
data class StepConfig(
    val name: String,
    val validation: String? = null,
    val validationJobPrefix: Boolean = true,
    val description: String? = null,
)

/**
 * Job level configuration
 *
 * @param name Exact name of the job in the workflow
 * @param validation Name of the validation stamp to use (instead of a generated one)
 * @param description Description for the validation stamp
 */
data class JobConfig(
    val name: String,
    val validation: String? = null,
    val description: String? = null,
)
