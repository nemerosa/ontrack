package net.nemerosa.ontrack.extension.indicators.ui

import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.extension.indicators.model.CreateTypeForm
import net.nemerosa.ontrack.extension.indicators.model.IndicatorReportingFilter
import net.nemerosa.ontrack.extension.indicators.model.IndicatorTypeIdMismatchException
import net.nemerosa.ontrack.extension.indicators.model.IndicatorTypeService
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import net.nemerosa.ontrack.ui.resource.Resource
import net.nemerosa.ontrack.ui.resource.Resources
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

/**
 * Controller for the management of types.
 */
@RestController
@RequestMapping("/extension/indicators/types")
class IndicatorTypeController(
    private val indicatorTypeService: IndicatorTypeService,
    private val indicatorExportService: IndicatorExportService
) : AbstractResourceController() {

    /**
     * Gets the list of types
     */
    @GetMapping("")
    fun findAll(): Resources<ProjectIndicatorType> =
        Resources.of(
            indicatorTypeService.findAll().map {
                ProjectIndicatorType(it)
            },
            uri(on(this::class.java).findAll())
        )

    @PostMapping("create")
    fun createType(@RequestBody @Valid input: CreateTypeForm): Resource<ProjectIndicatorType> =
        getTypeById(indicatorTypeService.createType(input).id)

    @PutMapping("{id}/update")
    fun updateType(
        @PathVariable id: String,
        @RequestBody @Valid input: CreateTypeForm
    ): Resource<ProjectIndicatorType> {
        if (id != input.id) {
            throw IndicatorTypeIdMismatchException(id, input.id)
        }
        return getTypeById(indicatorTypeService.updateType(input).id)
    }

    @DeleteMapping("{id}/delete")
    fun deleteType(@PathVariable id: String): ResponseEntity<Ack> =
        ResponseEntity.ok(indicatorTypeService.deleteType(id))

    /**
     * Download the type report as CSV
     */
    @GetMapping("{id}/report/export")
    fun reportExport(
        @PathVariable id: String,
        @RequestParam(value = "filledOnly", defaultValue = "true") filledOnly: Boolean,
        response: HttpServletResponse
    ): Document {
        // Gets the type
        val type = indicatorTypeService.getTypeById(id)
        // Filter on the projects
        val filter = IndicatorReportingFilter(filledOnly = filledOnly)
        // Export to CSV
        val csv = indicatorExportService.exportCSV(filter, listOf(type))
        // Attachment
        response.addHeader("Content-Disposition", "attachment; filename=ontrack-indicator-type-$id.csv")
        // Export as CSV
        return csv
    }

    @GetMapping("{id}")
    fun getTypeById(@PathVariable id: String): Resource<ProjectIndicatorType> =
        Resource.of(
            ProjectIndicatorType(indicatorTypeService.getTypeById(id)),
            uri(on(this::class.java).getTypeById(id))
        )

}