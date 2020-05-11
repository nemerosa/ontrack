package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.extension.indicators.acl.IndicatorTypeManagement
import net.nemerosa.ontrack.extension.indicators.model.*
import net.nemerosa.ontrack.model.form.*
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ServiceConfigurationSource
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import net.nemerosa.ontrack.ui.resource.Link
import net.nemerosa.ontrack.ui.resource.Resource
import net.nemerosa.ontrack.ui.resource.Resources
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

/**
 * Controller for the management of types.
 */
@RestController
@RequestMapping("/extension/indicators/types")
class IndicatorTypeController(
        private val indicatorTypeService: IndicatorTypeService,
        private val indicatorCategoryService: IndicatorCategoryService,
        private val indicatorValueTypeService: IndicatorValueTypeService,
        private val securityService: SecurityService
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
            ).with(
                    Link.CREATE,
                    uri(on(this::class.java).getCreationForm()),
                    securityService.isGlobalFunctionGranted(IndicatorTypeManagement::class.java)
            )

    /**
     * Gets the creation form for a type
     */
    @GetMapping("create")
    fun getCreationForm(): Form = Form.create()
            .with(
                    Selection.of(CreateTypeForm::category.name)
                            .label("Indicator category")
                            .items(indicatorCategoryService.findAll())
                            .itemId(IndicatorCategory::id.name)
                            .itemName(IndicatorCategory::name.name)
            )
            .with(
                    Text.of(CreateTypeForm::shortName.name)
                            .label("Short name")
            )
            .with(
                    Text.of(CreateTypeForm::longName.name)
                            .label("Long name")
            )
            .with(
                    Url.of(CreateTypeForm::link.name)
                            .label("Link to longer description")
                            .optional()
            )
            .with(
                    ServiceConfigurator.of(CreateTypeForm::valueType.name)
                            .label("Value type")
                            .sources(
                                    indicatorValueTypeService.findAll()
                                            .map { valueType ->
                                                ServiceConfigurationSource(
                                                        valueType.id,
                                                        valueType.name,
                                                        valueType.configForm(null)
                                                )
                                            }
                            )
            )

    @PostMapping("create")
    fun createType(@RequestBody input: CreateTypeForm): Resource<ProjectIndicatorType> =
            getTypeById(indicatorTypeService.createType(input).id)

    @GetMapping("{id}")
    fun getTypeById(@PathVariable id: String): Resource<ProjectIndicatorType> =
            Resource.of(
                    ProjectIndicatorType(indicatorTypeService.getTypeById(id)),
                    uri(on(this::class.java).getTypeById(id))
            )

}