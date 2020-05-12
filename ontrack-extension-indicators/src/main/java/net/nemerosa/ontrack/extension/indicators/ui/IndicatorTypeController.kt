package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.extension.indicators.acl.IndicatorTypeManagement
import net.nemerosa.ontrack.extension.indicators.model.*
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.form.*
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ServiceConfiguration
import net.nemerosa.ontrack.model.structure.ServiceConfigurationSource
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import net.nemerosa.ontrack.ui.resource.Link
import net.nemerosa.ontrack.ui.resource.Resource
import net.nemerosa.ontrack.ui.resource.Resources
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on
import javax.validation.Valid

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
    fun getCreationForm(): Form = getTypeForm<Any, Any>()

    @PostMapping("create")
    fun createType(@RequestBody @Valid input: CreateTypeForm): Resource<ProjectIndicatorType> =
            getTypeById(indicatorTypeService.createType(input).id)

    @GetMapping("{id}/update")
    fun getUpdateForm(@PathVariable id: String): Form {
        val type = indicatorTypeService.getTypeById(id)
        return getTypeForm(type)
    }

    @PutMapping("{id}/update")
    fun updateType(@PathVariable id: String, @RequestBody @Valid input: CreateTypeForm): Resource<ProjectIndicatorType> {
        if (id != input.id) {
            throw IndicatorTypeIdMismatchException(id, input.id)
        }
        return getTypeById(indicatorTypeService.updateType(input).id)
    }

    @DeleteMapping("{id}/delete")
    fun deleteType(@PathVariable id: String): ResponseEntity<Ack> =
            ResponseEntity.ok(indicatorTypeService.deleteType(id))

    private fun <T, C> getTypeForm(type: IndicatorType<T, C>? = null): Form {
        return Form.create()
                .with(
                        Text.of("id")
                                .label("ID")
                                .regex(CREATE_TYPE_FORM_ID_PATTERN)
                                .readOnly(type != null)
                                .value(type?.id)
                )
                .with(
                        Selection.of(CreateTypeForm::category.name)
                                .label("Indicator category")
                                .items(indicatorCategoryService.findAll())
                                .itemId(IndicatorCategory::id.name)
                                .itemName(IndicatorCategory::name.name)
                                .value(type?.category?.id)
                )
                .with(
                        Text.of(CreateTypeForm::shortName.name)
                                .label("Short name")
                                .value(type?.shortName)
                )
                .with(
                        Text.of(CreateTypeForm::longName.name)
                                .label("Long name")
                                .value(type?.longName)
                )
                .with(
                        Url.of(CreateTypeForm::link.name)
                                .label("Link to longer description")
                                .optional()
                                .value(type?.link)
                )
                .with(
                        ServiceConfigurator.of(CreateTypeForm::valueType.name)
                                .label("Value type")
                                .readOnly(type != null)
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
                                .value(type?.let { toValueTypeConfiguration(it) })
                )
    }

    private fun <T, C> toValueTypeConfiguration(type: IndicatorType<T, C>) = ServiceConfiguration(
            type.valueType.id,
            type.valueType.toConfigForm(type.valueConfig)
    )

    @GetMapping("{id}")
    fun getTypeById(@PathVariable id: String): Resource<ProjectIndicatorType> =
            Resource.of(
                    ProjectIndicatorType(indicatorTypeService.getTypeById(id)),
                    uri(on(this::class.java).getTypeById(id))
            )

}