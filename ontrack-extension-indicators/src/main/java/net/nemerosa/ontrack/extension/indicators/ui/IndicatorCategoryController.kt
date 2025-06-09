package net.nemerosa.ontrack.extension.indicators.ui

import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.extension.indicators.model.*
import net.nemerosa.ontrack.model.Ack
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Controller for the management of categories.
 */
@RestController
@RequestMapping("/extension/indicators/categories")
class IndicatorCategoryController(
    private val indicatorCategoryService: IndicatorCategoryService,
    private val indicatorTypeService: IndicatorTypeService,
    private val indicatorExportService: IndicatorExportService
) {

    /**
     * Gets the list of categories
     */
    @GetMapping("")
    fun findAll(): ResponseEntity<List<IndicatorCategory>> =
        ResponseEntity.ok(
            indicatorCategoryService.findAll()
        )

    @PostMapping("create")
    fun createType(@RequestBody @Valid input: IndicatorForm): ResponseEntity<IndicatorCategory> =
        getCategoryById(indicatorCategoryService.createCategory(input).id)

    @GetMapping("{id}")
    fun getCategoryById(@PathVariable id: String): ResponseEntity<IndicatorCategory> =
        ResponseEntity.ok(
            indicatorCategoryService.getCategory(id)
        )

    @PutMapping("{id}/update")
    fun updateCategory(
        @PathVariable id: String,
        @RequestBody @Valid input: IndicatorForm
    ): ResponseEntity<IndicatorCategory> {
        if (id != input.id) {
            throw IndicatorCategoryIdMismatchException(id, input.id)
        }
        return getCategoryById(indicatorCategoryService.updateCategory(input).id)
    }

    @DeleteMapping("{id}/delete")
    fun deleteCategory(@PathVariable id: String): ResponseEntity<Ack> =
        ResponseEntity.ok(indicatorCategoryService.deleteCategory(id))

    /**
     * Download the category report as CSV
     */
    @GetMapping("{id}/report/export")
    fun reportExport(
        @PathVariable id: String,
        @RequestParam(value = "filledOnly", defaultValue = "true") filledOnly: Boolean,
        response: HttpServletResponse
    ): Document {
        // Gets the category
        val category = indicatorCategoryService.getCategory(id)
        // Gets the types for this category
        val types = indicatorTypeService.findByCategory(category)
        // Filter on the projects
        val filter = IndicatorReportingFilter(filledOnly = filledOnly)
        // Export
        val csv = indicatorExportService.exportCSV(filter, types)
        // Attachment
        response.addHeader("Content-Disposition", "attachment; filename=ontrack-indicator-category-$id.csv")
        // Export as CSV
        return csv
    }

}