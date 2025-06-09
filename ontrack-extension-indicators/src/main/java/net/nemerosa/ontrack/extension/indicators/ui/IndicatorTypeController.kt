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
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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
    fun findAll(): ResponseEntity<List<ProjectIndicatorType>> =
        ResponseEntity.ok(
            indicatorTypeService.findAll().map {
                ProjectIndicatorType(it)
            },
        )

    @PostMapping("create")
    fun createType(@RequestBody @Valid input: CreateTypeForm): ResponseEntity<ProjectIndicatorType> =
        getTypeById(indicatorTypeService.createType(input).id)

    @PutMapping("{id}/update")
    fun updateType(
        @PathVariable id: String,
        @RequestBody @Valid input: CreateTypeForm
    ): ResponseEntity<ProjectIndicatorType> {
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
    fun getTypeById(@PathVariable id: String): ResponseEntity<ProjectIndicatorType> =
        ResponseEntity.ok(
            ProjectIndicatorType(indicatorTypeService.getTypeById(id)),
        )

}