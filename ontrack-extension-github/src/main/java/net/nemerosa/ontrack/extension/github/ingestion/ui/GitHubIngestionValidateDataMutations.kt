package net.nemerosa.ontrack.extension.github.ingestion.ui

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.github.ingestion.processing.config.ConfigService
import net.nemerosa.ontrack.extension.github.ingestion.processing.config.INGESTION_CONFIG_FILE_PATH
import net.nemerosa.ontrack.extension.github.ingestion.processing.job.WorkflowJobProcessingService
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository
import net.nemerosa.ontrack.extension.github.ingestion.settings.GitHubIngestionSettings
import net.nemerosa.ontrack.extension.github.ingestion.support.IngestionModelAccessService
import net.nemerosa.ontrack.graphql.schema.Mutation
import net.nemerosa.ontrack.graphql.support.TypeRef
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component

/**
 * Mutations used to inject data into validations.
 */
@Component
class GitHubIngestionValidateDataMutations(
    private val ingestionModelAccessService: IngestionModelAccessService,
    private val cachedSettingsService: CachedSettingsService,
    private val configService: ConfigService,
    private val structureService: StructureService,
    private val workflowJobProcessingService: WorkflowJobProcessingService,
    private val validationRunStatusService: ValidationRunStatusService,
) : TypedMutationProvider() {

    override val mutations: List<Mutation> = listOf(
        /**
         * Getting the build by run ID
         */
        simpleMutation(
            name = "gitHubIngestionValidateDataByRunId",
            description = "Sets some validation data on a build identified using a GHA workflow run ID",
            input = GitHubIngestionValidateDataByRunIdInput::class,
            outputName = "validationRun",
            outputDescription = "Created validation run",
            outputType = ValidationRun::class
        ) { input ->
            val build = findBuildByRunId(input, input.runId)
            build?.run { validate(this, input) }
        },
    )

    private fun validate(build: Build, input: GitHubIngestionValidateDataByRunIdInput): ValidationRun? {
        // Gets the general ingestion settings
        val settings = cachedSettingsService.getCachedSettings(GitHubIngestionSettings::class.java)
        // Gets the branch ingestion settings
        val config = configService.getOrLoadConfig(build.branch, INGESTION_CONFIG_FILE_PATH)
        // Setting up the validation stamp
        val vs = workflowJobProcessingService.setupValidationStamp(
            branch = build.branch,
            vsName = input.validation,
            vsDescription = null,
        )
        // Gets any existing validation run
        val run = structureService.getValidationRunsForBuildAndValidationStamp(
            buildId = build.id,
            validationStampId = vs.id,
            offset = 0,
            count = 1,
        ).firstOrNull()
        // If the run already exists, set its data
        return if (run != null) {
            TODO("Sets the data on an existing validation run")
            // OK
            run
        } else {
            // Validation status
            val validationRunStatusId = input.validationStatus?.run {
                validationRunStatusService.getValidationRunStatus(this)
            }
            // Creates the validation run
            structureService.newValidationRun(
                build = build,
                validationRunRequest = ValidationRunRequest(
                    validationStampName = input.validation,
                    validationRunStatusId = validationRunStatusId,
                    dataTypeId = input.validationData.type,
                    data = input.validationData.data,
                )
            )
        }
    }

    private fun findBuildByRunId(input: AbstractGitHubIngestionValidateDataInput, runId: Long): Build? =
        ingestionModelAccessService.findBuildByRunId(
            repository = Repository.stub(input.owner, input.repository),
            runId = runId,
        )
}

/**
 * Common data between all the inputs
 */
abstract class AbstractGitHubIngestionValidateDataInput(
    @APIDescription("Name of the repository owner to target")
    val owner: String,
    @APIDescription("Name of the repository to target")
    val repository: String,
    // @APIDescription("GitHub ref to target (refs/heads/...)")
    // val ref: String,
    @APIDescription("Name of the validation stamp to create")
    val validation: String,
    @APIDescription("Validation data")
    @TypeRef(embedded = true)
    val validationData: GitHubIngestionValidationDataInput,
    @APIDescription("Optional validation status")
    val validationStatus: String?,
)

/**
 * Input for the data validation for a build identified by ID
 */
class GitHubIngestionValidateDataByRunIdInput(
    owner: String,
    repository: String,
    // ref: String,
    validation: String,
    validationData: GitHubIngestionValidationDataInput,
    validationStatus: String?,
    @APIDescription("ID of the GHA workflow run")
    val runId: Long,
) : AbstractGitHubIngestionValidateDataInput(
    owner,
    repository,
    // ref,
    validation,
    validationData,
    validationStatus,
)

/**
 * Validation data input
 */
data class GitHubIngestionValidationDataInput(
    @APIDescription("FQCN of the validation data type")
    val type: String,
    @APIDescription("Validation data")
    val data: JsonNode,
)
