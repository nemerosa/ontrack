package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Selection
import net.nemerosa.ontrack.model.form.ServiceConfigurator
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import net.nemerosa.ontrack.ui.resource.Pagination
import net.nemerosa.ontrack.ui.resource.PaginationCountException
import net.nemerosa.ontrack.ui.resource.PaginationOffsetException
import net.nemerosa.ontrack.ui.resource.Resources
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@RestController
@RequestMapping("/structure")
class ValidationRunController
@Autowired
constructor(
        private val structureService: StructureService,
        private val validationRunStatusService: ValidationRunStatusService,
        private val validationDataTypeService: ValidationDataTypeService,
        private val propertyService: PropertyService,
        private val securityService: SecurityService
) : AbstractResourceController() {

    @GetMapping("builds/{buildId}/validationRuns/view")
    fun getValidationStampRunViews(@PathVariable buildId: ID): Resources<ValidationStampRunView> {
        // Build
        val build = structureService.getBuild(buildId)
        // Gets the views
        val views = structureService.getValidationStampRunViewsForBuild(build)
        // Converts into a view
        val uri = uri(on(javaClass).getValidationStampRunViews(buildId))
        return Resources.of(
                views,
                uri
        ).forView(ValidationStampRunView::class.java)
    }

    @GetMapping("builds/{buildId}/validationRuns")
    fun getValidationRuns(@PathVariable buildId: ID): Resources<ValidationRun> {
        return Resources.of(
                structureService.getValidationRunsForBuild(buildId),
                uri(on(ValidationRunController::class.java).getValidationRuns(buildId))
        ).forView(Build::class.java)
    }

    @GetMapping("builds/{buildId}/validationRuns/create")
    fun newValidationRunForm(@PathVariable buildId: ID): Form {
        val build = structureService.getBuild(buildId)
        return Form.create()
                .with(
                        ServiceConfigurator.of("validationStampData")
                                .label("Validation stamp")
                                .sources(
                                        structureService.getValidationStampListForBranch(build.branch.id)
                                                .map {
                                                    ServiceConfigurationSource(
                                                            it.name,
                                                            it.name,
                                                            it.dataType?.let { dataType ->
                                                                validationDataTypeService
                                                                        .getValidationDataType<Any, Any>(dataType.id)
                                                                        ?.getForm(null)
                                                            } ?: Form.create()
                                                    )
                                                }
                                )
                )
                .with(
                        Selection.of("validationRunStatusId")
                                .label("Status")
                                .items(validationRunStatusService.validationRunStatusRoots)
                )
                .description()
    }

    @PostMapping("builds/{buildId}/validationRuns/create")
    @ResponseStatus(HttpStatus.CREATED)
    fun newValidationRun(@PathVariable buildId: ID, @RequestBody validationRunRequest: ValidationRunRequest): ValidationRun {
        // Gets the build
        val build = structureService.getBuild(buildId)
        // Gets the validation stamp
        val validationStamp = getValidationStamp(
                build.branch,
                validationRunRequest.validationStampId,
                validationRunRequest.actualValidationStampName)
        // Gets the validation run status
        val validationRunStatusID = validationRunStatusService.getValidationRunStatus(validationRunRequest.validationRunStatusId)
        // Validation run to create
        var validationRun = ValidationRun.of(
                build,
                validationStamp,
                0,
                securityService.currentSignature,
                validationRunStatusID,
                validationRunRequest.description
        )
        // Validation run data
        val data: ValidationRunData<Any>? = validationDataTypeService.validateData(
                validationRunRequest.validationStampData,
                validationStamp.dataType
        )
        validationRun = validationRun.withData(data)
        // Creation
        validationRun = structureService.newValidationRun(validationRun)
        // Saves the properties
        for ((propertyTypeName, propertyData) in validationRunRequest.properties) {
            propertyService.editProperty(
                    validationRun,
                    propertyTypeName,
                    propertyData
            )
        }
        // OK
        return validationRun
    }

    protected fun getValidationStamp(branch: Branch, validationStampId: Int?, validationStampName: String?): ValidationStamp {
        return structureService.getOrCreateValidationStamp(branch, validationStampId, validationStampName)
    }

    @GetMapping("validationRuns/{validationRunId}")
    fun getValidationRun(@PathVariable validationRunId: ID): ValidationRun =
            structureService.getValidationRun(validationRunId)

    // Validation run status

    @GetMapping(value = "validationRuns/{validationRunId}/status/change")
    fun getValidationRunStatusChangeForm(@PathVariable validationRunId: ID): Form {
        val validationRun = structureService.getValidationRun(validationRunId)
        return Form.create()
                .with(
                        Selection.of("validationRunStatusId")
                                .label("Status")
                                .items(
                                        validationRunStatusService.getNextValidationRunStatusList(validationRun.lastStatus.statusID.id)
                                )
                )
                .description()
    }

    @PostMapping("validationRuns/{validationRunId}/status/change")
    @ResponseStatus(HttpStatus.CREATED)
    fun validationRunStatusChange(@PathVariable validationRunId: ID, @RequestBody request: ValidationRunStatusChangeRequest): ValidationRun {
        // Gets the current run
        val run = structureService.getValidationRun(validationRunId)
        // Gets the new validation run status
        val runStatus = ValidationRunStatus.of(
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
    @GetMapping(value = "validationStamps/{validationStampId}/validationRuns")
    fun getValidationRunsForValidationStamp(
            @PathVariable validationStampId: ID,
            @RequestParam(required = false, defaultValue = "0") offset: Int,
            @RequestParam(required = false, defaultValue = "10") count: Int): Resources<ValidationRun> {
        // Gets ALL the runs
        val runs = structureService.getValidationRunsForValidationStamp(validationStampId, 0, Integer.MAX_VALUE)
        // Total number of runs
        val total = runs.size
        // Checks the offset and count
        if (offset < 0) {
            throw PaginationOffsetException(offset)
        } else if (offset > 0 && offset >= total) {
            throw PaginationOffsetException(offset)
        } else if (count <= 0) {
            throw PaginationCountException(count)
        }
        // Prepares the resources
        val resources = Resources.of(
                runs.subList(offset, Math.min(offset + count, runs.size)),
                uri(on(ValidationRunController::class.java).getValidationRunsForValidationStamp(
                        validationStampId,
                        offset,
                        count
                ))
        )
        // Pagination information
        var pagination = Pagination.of(offset, count, total)
        // Previous page
        if (offset > 0) {
            pagination = pagination.withPrev(
                    uri(on(ValidationRunController::class.java).getValidationRunsForValidationStamp(
                            validationStampId,
                            Math.max(0, offset - count),
                            count
                    ))
            )
        }
        // Next page
        if (offset + count < total) {
            pagination = pagination.withNext(
                    uri(on(ValidationRunController::class.java).getValidationRunsForValidationStamp(
                            validationStampId,
                            offset + count,
                            count
                    ))
            )
        }
        // OK
        return resources.withPagination(pagination).forView(ValidationStampRunView::class.java)
    }

}
