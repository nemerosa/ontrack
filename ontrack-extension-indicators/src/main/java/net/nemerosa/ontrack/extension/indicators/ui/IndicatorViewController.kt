package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.extension.indicators.acl.IndicatorViewManagement
import net.nemerosa.ontrack.extension.indicators.model.IndicatorCategoryService
import net.nemerosa.ontrack.extension.indicators.model.IndicatorReportingFilter
import net.nemerosa.ontrack.extension.indicators.model.IndicatorTypeService
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorView
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorViewIDNotFoundException
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorViewService
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Text
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import net.nemerosa.ontrack.ui.resource.Link
import net.nemerosa.ontrack.ui.resource.Resource
import net.nemerosa.ontrack.ui.resource.Resources
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on
import javax.servlet.http.HttpServletResponse
import jakarta.validation.Valid

/**
 * Controller for the management of indicator views.
 */
@RestController
@RequestMapping("/extension/indicators/views")
class IndicatorViewController(
    private val indicatorViewService: IndicatorViewService,
    private val indicatorCategoryService: IndicatorCategoryService,
    private val indicatorTypeService: IndicatorTypeService,
    private val indicatorExportService: IndicatorExportService,
    private val securityService: SecurityService
) : AbstractResourceController() {

    /**
     * Gets the list of views
     */
    @GetMapping("")
    fun findAll(): Resources<IndicatorView> =
        Resources.of(
            indicatorViewService.getIndicatorViews(),
            uri(on(this::class.java).findAll())
        ).with(
            Link.CREATE,
            uri(on(this::class.java).getCreationForm()),
            securityService.isGlobalFunctionGranted(IndicatorViewManagement::class.java)
        )

    /**
     * Gets the creation form for a view
     */
    @GetMapping("create")
    fun getCreationForm(): Form = getViewForm()

    @PostMapping("create")
    fun create(@RequestBody @Valid input: IndicatorViewForm): Resource<IndicatorView> =
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
    fun getViewById(@PathVariable id: String): Resource<IndicatorView> =
        Resource.of(
            indicatorViewService.findIndicatorViewById(id) ?: throw IndicatorViewIDNotFoundException(id),
            uri(on(this::class.java).getViewById(id))
        )

    @GetMapping("{id}/update")
    fun getUpdateForm(@PathVariable id: String): Form {
        val view = indicatorViewService.findIndicatorViewById(id) ?: throw IndicatorViewIDNotFoundException(id)
        return getViewForm(view)
    }

    @PutMapping("{id}/update")
    fun update(
        @PathVariable id: String,
        @RequestBody @Valid input: IndicatorViewForm
    ): Resource<IndicatorView> {
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

    private fun getViewForm(view: IndicatorView? = null): Form {
        return Form.create()
            .with(
                Text.of(IndicatorView::name.name)
                    .label("Name")
                    .help("Unique name of the view")
                    .value(view?.name)
            )
    }

    class IndicatorViewForm(
        val name: String,
        val categories: List<String>?
    )

}