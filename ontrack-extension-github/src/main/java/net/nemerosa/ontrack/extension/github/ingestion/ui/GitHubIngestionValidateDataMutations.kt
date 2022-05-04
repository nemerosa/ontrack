package net.nemerosa.ontrack.extension.github.ingestion.ui

import net.nemerosa.ontrack.extension.github.ingestion.processing.job.WorkflowJobProcessingService
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository
import net.nemerosa.ontrack.extension.github.ingestion.support.IngestionModelAccessService
import net.nemerosa.ontrack.extension.github.ingestion.validation.*
import net.nemerosa.ontrack.graphql.schema.Mutation
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.model.exceptions.ValidationRunDataTypeNotFoundException
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component

/**
 * Mutations used to inject data into validations.
 */
@Component
class GitHubIngestionValidateDataMutations(
    private val ingestionModelAccessService: IngestionModelAccessService,
    private val structureService: StructureService,
    private val runInfoService: RunInfoService,
    private val workflowJobProcessingService: WorkflowJobProcessingService,
    private val validationRunStatusService: ValidationRunStatusService,
    private val validationDataTypeService: ValidationDataTypeService,
    private val ingestionValidateDataService: IngestionValidateDataService,
) : TypedMutationProvider() {

    override val mutations: List<Mutation> = listOf(
        /**
         * Getting the build by run ID
         */
        unitMutation<GitHubIngestionValidateDataByRunIdInput>(
            name = "gitHubIngestionValidateDataByRunId",
            description = "Sets some validation data on a build identified using a GHA workflow run ID",
        ) { input ->
            ingestionValidateDataService.ingestValidationData(input)
        },
        /**
         * Getting the build by build name
         */
        unitMutation<GitHubIngestionValidateDataByBuildNameInput>(
            name = "gitHubIngestionValidateDataByBuildName",
            description = "Sets some validation data on a build identified using its name",
        ) { input ->
            ingestionValidateDataService.ingestValidationData(input)
        },
        /**
         * Getting the build by build label
         */
        unitMutation<GitHubIngestionValidateDataByBuildLabelInput>(
            name = "gitHubIngestionValidateDataByBuildLabel",
            description = "Sets some validation data on a build identified using its release property (label)",
        ) { input ->
            ingestionValidateDataService.ingestValidationData(input)
        },
    )

    private fun validate(build: Build, input: AbstractGitHubIngestionValidateDataInput): ValidationRun {
        // Setting up the validation stamp
        val vs = workflowJobProcessingService.setupValidationStamp(
            branch = build.branch,
            vsName = input.validation,
            vsDescription = null,
        )
        // Parsing the data
        val validationDataType = validationDataTypeService.getValidationDataType<Any, Any>(input.validationData.type)
            ?: throw ValidationRunDataTypeNotFoundException(input.validationData.type)
        val parsedData = validationDataType.fromForm(input.validationData.data)
        // Gets any existing validation run
        val run = structureService.getValidationRunsForBuildAndValidationStamp(
            buildId = build.id,
            validationStampId = vs.id,
            offset = 0,
            count = 1,
        ).firstOrNull()
        // Any existing run info
        var existingRunInfo: RunInfoInput? = null
        // If the run already exists, takes its run info and remove it
        if (run != null) {
            existingRunInfo = runInfoService.getRunInfo(run)?.toRunInfoInput()
            structureService.deleteValidationRun(run)
        }
        // Validation status
        val validationRunStatusId = input.validationStatus?.run {
            validationRunStatusService.getValidationRunStatus(this)
        }
        // Creates the validation run
        val validationRun = structureService.newValidationRun(
            build = build,
            validationRunRequest = ValidationRunRequest(
                validationStampName = input.validation,
                validationRunStatusId = validationRunStatusId,
                dataTypeId = input.validationData.type,
                data = parsedData,
            )
        )
        // Run info
        if (existingRunInfo != null) {
            runInfoService.setRunInfo(
                validationRun,
                existingRunInfo
            )
        }
        // OK
        return validationRun
    }

    private fun findBuildByRunId(input: AbstractGitHubIngestionValidateDataInput, runId: Long): Build? =
        ingestionModelAccessService.findBuildByRunId(
            repository = Repository.stub(input.owner, input.repository),
            runId = runId,
        )

    private fun findBuildByBuildName(input: AbstractGitHubIngestionValidateDataInput, buildName: String): Build? =
        ingestionModelAccessService.findBuildByBuildName(
            repository = Repository.stub(input.owner, input.repository),
            buildName = buildName,
        )

    private fun findBuildByBuildLabel(input: AbstractGitHubIngestionValidateDataInput, buildLabel: String): Build? =
        ingestionModelAccessService.findBuildByBuildLabel(
            repository = Repository.stub(input.owner, input.repository),
            buildLabel = buildLabel,
        )
}

