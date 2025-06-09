package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.json.JsonParseException
import net.nemerosa.ontrack.model.exceptions.ValidationRunDataJSONInputException
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import kotlin.math.min

@RestController
@RequestMapping("/rest/structure")
class ValidationRunController
@Autowired
constructor(
    private val structureService: StructureService,
    private val validationRunStatusService: ValidationRunStatusService,
    private val validationDataTypeService: ValidationDataTypeService,
    private val securityService: SecurityService
) : AbstractResourceController() {

    @GetMapping("builds/{buildId}/validationRuns/view")
    fun getValidationStampRunViews(@PathVariable buildId: ID): List<ValidationStampRunView> {
        // Build
        val build = structureService.getBuild(buildId)
        // Gets the views
        val views = structureService.getValidationStampRunViewsForBuild(build)
        // Converts into a view
        return views
    }

    @GetMapping("builds/{buildId}/validationRuns")
    fun getValidationRuns(@PathVariable buildId: ID): List<ValidationRun> {
        return structureService.getValidationRunsForBuild(buildId, 0, 100)
    }

    /**
     * Note that some properties, like the `type` of data are provided only when using the DSL
     * or the raw REST API, never through the GUI.
     */
    @PostMapping("builds/{buildId}/validationRuns/create")
    @ResponseStatus(HttpStatus.CREATED)
    fun newValidationRun(
        @PathVariable buildId: ID,
        @RequestBody validationRunRequestForm: ValidationRunRequestForm
    ): ValidationRun {
        // Gets the build
        val build = structureService.getBuild(buildId)
        // Creates the service validation run request from the form
        val validationRunRequest = ValidationRunRequest(
            validationStampName = validationRunRequestForm.actualValidationStampName,
            validationRunStatusId = validationRunRequestForm.validationRunStatusId?.run {
                validationRunStatusService.getValidationRunStatus(this)
            },
            dataTypeId = validationRunRequestForm.validationStampData?.type,
            data = parseValidationRunData(build, validationRunRequestForm),
            description = validationRunRequestForm.description,
            properties = validationRunRequestForm.properties
        )
        // Delegates to the service
        return structureService.newValidationRun(build, validationRunRequest)
    }

    private fun parseValidationRunData(build: Build, validationRunRequestForm: ValidationRunRequestForm): Any? {
        return validationRunRequestForm.validationStampData?.data?.run {
            // Gets the validation stamp
            val validationStamp: ValidationStamp = structureService.getOrCreateValidationStamp(
                build.branch,
                validationRunRequestForm.actualValidationStampName
            )
            // Gets the data type ID if any
            // First, the data type in the request, and if not specified, the type of the validation stamp
            val typeId: String? = validationRunRequestForm.validationStampData.type
                ?: validationStamp.dataType?.descriptor?.id
            // If no type, ignore the data
            return typeId?.run {
                // Gets the actual type
                validationDataTypeService.getValidationDataType<Any, Any>(this)
            }?.run {
                // Parses data from the form
                try {
                    fromForm(validationRunRequestForm.validationStampData.data)
                } catch (ex: JsonParseException) {
                    throw ValidationRunDataJSONInputException(ex, validationRunRequestForm.validationStampData.data)
                }
            }
        }
    }

    @GetMapping("validationRuns/{validationRunId}")
    fun getValidationRun(@PathVariable validationRunId: ID): ValidationRun =
        structureService.getValidationRun(validationRunId)

    // Validation run status

    @PostMapping("validationRuns/{validationRunId}/status/change")
    @ResponseStatus(HttpStatus.CREATED)
    fun validationRunStatusChange(
        @PathVariable validationRunId: ID,
        @RequestBody request: ValidationRunStatusChangeRequest
    ): ValidationRun {
        // Gets the current run
        val run = structureService.getValidationRun(validationRunId)
        // Gets the new validation run status
        val runStatus = ValidationRunStatus(
            ID.NONE,
            securityService.currentSignature,
            validationRunStatusService.getValidationRunStatus(request.validationRunStatusId),
            request.description
        )
        // Updates the validation run
        return structureService.newValidationRunStatus(run, runStatus)
    }

    /**
     * List of validation runs for a validation stamp
     */
    @GetMapping("validationStamps/{validationStampId}/validationRuns")
    fun getValidationRunsForValidationStamp(
        @PathVariable validationStampId: ID,
        @RequestParam(required = false, defaultValue = "0") offset: Int,
        @RequestParam(required = false, defaultValue = "10") count: Int
    ): List<ValidationRun> {
        // Gets ALL the runs
        val runs = structureService.getValidationRunsForValidationStamp(validationStampId, 0, Integer.MAX_VALUE)
        // Prepares the resources
        return runs.subList(offset, min(offset + count, runs.size))
    }

}
