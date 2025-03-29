package net.nemerosa.ontrack.extension.indicators.ui

import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.extension.indicators.acl.IndicatorTypeManagement
import net.nemerosa.ontrack.extension.indicators.model.*
import net.nemerosa.ontrack.extension.indicators.model.IndicatorConstants.INDICATOR_ID_PATTERN
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

/**
 * Controller for the management of categories.
 */
@RestController
@RequestMapping("/extension/indicators/categories")
class IndicatorCategoryController(
    private val indicatorCategoryService: IndicatorCategoryService,
    private val indicatorTypeService: IndicatorTypeService,
    private val indicatorExportService: IndicatorExportService,
    private val securityService: SecurityService
) : AbstractResourceController() {

    /**
     * Gets the list of categories
     */
    @GetMapping("")
    fun findAll(): Resources<IndicatorCategory> =
        Resources.of(
            indicatorCategoryService.findAll(),
            uri(on(this::class.java).findAll())
        ).with(
            Link.CREATE,
            uri(on(this::class.java).getCreationForm()),
            securityService.isGlobalFunctionGranted(IndicatorTypeManagement::class.java)
        )

    /**
     * Gets the creation form for a category
     */
    @GetMapping("create")
    fun getCreationForm(): Form = getCategoryForm()

    @PostMapping("create")
    fun createType(@RequestBody @Valid input: IndicatorForm): Resource<IndicatorCategory> =
        getCategoryById(indicatorCategoryService.createCategory(input).id)

    @GetMapping("{id}")
    fun getCategoryById(@PathVariable id: String): Resource<IndicatorCategory> =
        Resource.of(
            indicatorCategoryService.getCategory(id),
            uri(on(this::class.java).getCategoryById(id))
        )

    @GetMapping("{id}/update")
    fun getUpdateForm(@PathVariable id: String): Form {
        val category = indicatorCategoryService.getCategory(id)
        return getCategoryForm(category)
    }

    @PutMapping("{id}/update")
    fun updateCategory(
        @PathVariable id: String,
        @RequestBody @Valid input: IndicatorForm
    ): Resource<IndicatorCategory> {
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

    private fun getCategoryForm(category: IndicatorCategory? = null): Form {
        return Form.create()
            .with(
                Text.of(IndicatorCategory::id.name)
                    .label("ID")
                    .help("ID for the category. Must be unique among ALL categories and comply with the $INDICATOR_ID_PATTERN regular expression.")
                    .regex(INDICATOR_ID_PATTERN)
                    .readOnly(category != null)
                    .value(category?.id)
            )
            .with(
                Text.of(IndicatorCategory::name.name)
                    .label("Name")
                    .help("Display name for the category.")
                    .value(category?.name)
            )
            .with(
                Text.of(IndicatorCategory::deprecated.name)
                    .label("Deprecated")
                    .help("If filled in, indicates that the category is deprecated and should not be used any longer.")
                    .optional()
                    .value(category?.deprecated)
            )
    }
}