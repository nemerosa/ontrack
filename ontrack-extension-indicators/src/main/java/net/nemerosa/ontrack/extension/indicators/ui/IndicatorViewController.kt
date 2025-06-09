package net.nemerosa.ontrack.extension.indicators.ui

import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.extension.indicators.model.IndicatorCategoryService
import net.nemerosa.ontrack.extension.indicators.model.IndicatorReportingFilter
import net.nemerosa.ontrack.extension.indicators.model.IndicatorTypeService
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorView
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorViewIDNotFoundException
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorViewService
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Controller for the management of indicator views.
 */
@RestController
@RequestMapping("/extension/indicators/views")
class IndicatorViewController(
    private val indicatorViewService: IndicatorViewService,
    private val indicatorCategoryService: IndicatorCategoryService,
    private val indicatorTypeService: IndicatorTypeService,
    private val indicatorExportService: IndicatorExportService
) : AbstractResourceController() {

    /**
     * Gets the list of views
     */
    @GetMapping("")
    fun findAll(): ResponseEntity<List<IndicatorView>> =
        ResponseEntity.ok(
            indicatorViewService.getIndicatorViews(),
        )

    @PostMapping("create")
    fun create(@RequestBody @Valid input: IndicatorViewForm): ResponseEntity<IndicatorView> =
        getViewById(
            indicatorViewService.saveIndicatorView(
                IndicatorView(
                    id = "",
                    name = input.name,
                    categories = input.categories ?: emptyList()
                )
            ).id
        )

    @GetMapping("{id}")
    fun getViewById(@PathVariable id: String): ResponseEntity<IndicatorView> =
        ResponseEntity.ok(
            indicatorViewService.findIndicatorViewById(id) ?: throw IndicatorViewIDNotFoundException(id),
        )

    @PutMapping("{id}/update")
    fun update(
        @PathVariable id: String,
        @RequestBody @Valid input: IndicatorViewForm
    ): ResponseEntity<IndicatorView> {
        return getViewById(
            indicatorViewService.saveIndicatorView(
                IndicatorView(
                    id = id,
                    name = input.name,
                    categories = input.categories ?: emptyList()
                )
            ).id
        )
    }

    @DeleteMapping("{id}/delete")
    fun delete(@PathVariable id: String): ResponseEntity<Ack> =
        ResponseEntity.ok(indicatorViewService.deleteIndicatorView(id))

    /**
     * Download the category report as CSV
     */
    @GetMapping("{id}/report/export")
    fun reportExport(
        @PathVariable id: String,
        @RequestParam(value = "filledOnly", defaultValue = "true") filledOnly: Boolean,
        response: HttpServletResponse
    ): Document {
        // Gets the view
        val view = indicatorViewService.findIndicatorViewById(id) ?: throw IndicatorViewIDNotFoundException(id)
        // Gets all the types for this view
        val types = view.categories
            .mapNotNull(indicatorCategoryService::findCategoryById)
            .flatMap(indicatorTypeService::findByCategory)
        // Filter on the projects
        val filter = IndicatorReportingFilter(filledOnly = filledOnly)
        // Export
        val csv = indicatorExportService.exportCSV(filter, types)
        // Attachment
        response.addHeader("Content-Disposition", "attachment; filename=ontrack-indicator-view-$id.csv")
        // Export as CSV
        return csv
    }

    class IndicatorViewForm(
        val name: String,
        val categories: List<String>?
    )

}