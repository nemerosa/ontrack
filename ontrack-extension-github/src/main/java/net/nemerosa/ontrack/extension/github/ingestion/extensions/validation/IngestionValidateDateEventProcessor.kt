package net.nemerosa.ontrack.extension.github.ingestion.extensions.validation

import net.nemerosa.ontrack.extension.github.ingestion.extensions.support.AbstractIngestionBuildEventProcessor
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventPreprocessingCheck
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
    private val validationRunStatusService: ValidationRunStatusService,
    private val validationDataTypeService: ValidationDataTypeService,
) : AbstractIngestionBuildEventProcessor<GitHubIngestionValidateDataPayload>(
    ingestionModelAccessService
) {

    override fun preProcessingCheck(payload: GitHubIngestionValidateDataPayload): IngestionEventPreprocessingCheck =
        IngestionEventPreprocessingCheck.TO_BE_PROCESSED

    override fun process(build: Build, input: GitHubIngestionValidateDataPayload) {
        // Setting up the validation stamp
        val vs = ingestionModelAccessService.setupValidationStamp(
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
    }

    override val payloadType: KClass<GitHubIngestionValidateDataPayload> =
        GitHubIngestionValidateDataPayload::class

    override val event: String = EVENT

    companion object {
        const val EVENT = "x-ontrack-validate-date"
    }
}