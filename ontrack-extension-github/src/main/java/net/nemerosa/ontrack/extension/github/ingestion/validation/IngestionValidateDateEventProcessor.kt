package net.nemerosa.ontrack.extension.github.ingestion.validation

import net.nemerosa.ontrack.extension.github.ingestion.processing.AbstractIngestionEventProcessor
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventPreprocessingCheck
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventProcessingResult
import net.nemerosa.ontrack.extension.github.ingestion.processing.job.WorkflowJobProcessingService
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository
import net.nemerosa.ontrack.extension.github.ingestion.support.IngestionModelAccessService
import net.nemerosa.ontrack.model.exceptions.ValidationRunDataTypeNotFoundException
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class IngestionValidateDateEventProcessor(
    private val ingestionModelAccessService: IngestionModelAccessService,
    private val structureService: StructureService,
    private val runInfoService: RunInfoService,
    private val workflowJobProcessingService: WorkflowJobProcessingService,
    private val validationRunStatusService: ValidationRunStatusService,
    private val validationDataTypeService: ValidationDataTypeService,
) : AbstractIngestionEventProcessor<GitHubIngestionValidateDataPayload>() {

    override fun getPayloadSource(payload: GitHubIngestionValidateDataPayload): String? {
        TODO("Not yet implemented")
    }

    override fun preProcessingCheck(payload: GitHubIngestionValidateDataPayload): IngestionEventPreprocessingCheck =
        IngestionEventPreprocessingCheck.TO_BE_PROCESSED

    override fun process(
        payload: GitHubIngestionValidateDataPayload,
        configuration: String?,
    ): IngestionEventProcessingResult {
        val build = if (payload.buildLabel != null) {
            findBuildByBuildLabel(payload, payload.buildLabel)
        } else if (payload.buildName != null) {
            findBuildByBuildName(payload, payload.buildName)
        } else if (payload.runId != null) {
            findBuildByRunId(payload, payload.runId)
        } else {
            error("Could not find any way to identify a build using $payload")
        }
        return if (build != null) {
            validate(build, payload)
            IngestionEventProcessingResult.PROCESSED
        } else {
            IngestionEventProcessingResult.IGNORED
        }
    }

    private fun validate(build: Build, input: GitHubIngestionValidateDataPayload): ValidationRun {
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

    override val payloadType: KClass<GitHubIngestionValidateDataPayload> =
        GitHubIngestionValidateDataPayload::class

    override val event: String = EVENT

    private fun findBuildByRunId(input: GitHubIngestionValidateDataPayload, runId: Long): Build? =
        ingestionModelAccessService.findBuildByRunId(
            repository = Repository.stub(input.owner, input.repository),
            runId = runId,
        )

    private fun findBuildByBuildName(input: GitHubIngestionValidateDataPayload, buildName: String): Build? =
        ingestionModelAccessService.findBuildByBuildName(
            repository = Repository.stub(input.owner, input.repository),
            buildName = buildName,
        )

    private fun findBuildByBuildLabel(input: GitHubIngestionValidateDataPayload, buildLabel: String): Build? =
        ingestionModelAccessService.findBuildByBuildLabel(
            repository = Repository.stub(input.owner, input.repository),
            buildLabel = buildLabel,
        )

    companion object {
        const val EVENT = "x-ontrack-validate-date"
    }
}