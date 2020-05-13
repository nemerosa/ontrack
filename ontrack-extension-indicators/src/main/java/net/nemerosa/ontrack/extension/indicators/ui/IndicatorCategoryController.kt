package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.extension.indicators.acl.IndicatorTypeManagement
import net.nemerosa.ontrack.extension.indicators.model.IndicatorCategory
import net.nemerosa.ontrack.extension.indicators.model.IndicatorCategoryService
import net.nemerosa.ontrack.extension.indicators.model.IndicatorConstants
import net.nemerosa.ontrack.extension.indicators.model.IndicatorForm
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Text
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import net.nemerosa.ontrack.ui.resource.Link
import net.nemerosa.ontrack.ui.resource.Resource
import net.nemerosa.ontrack.ui.resource.Resources
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on
import javax.validation.Valid

/**
 * Controller for the management of categories.
 */
@RestController
@RequestMapping("/extension/indicators/categories")
class IndicatorCategoryController(
        private val indicatorCategoryService: IndicatorCategoryService,
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


    private fun getCategoryForm(category: IndicatorCategory? = null): Form {
        return Form.create()
                .with(
                        Text.of(IndicatorCategory::id.name)
                                .label("ID")
                                .regex(IndicatorConstants.INDICATOR_ID_PATTERN)
                                .readOnly(category != null)
                                .value(category?.id)
                )
                .with(
                        Text.of(IndicatorCategory::name.name)
                                .label("Name")
                                .value(category?.name)
                )
    }
}