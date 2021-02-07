package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.extension.indicators.acl.IndicatorTypeManagement
import net.nemerosa.ontrack.extension.indicators.model.*
import net.nemerosa.ontrack.extension.indicators.model.IndicatorConstants.INDICATOR_ID_PATTERN
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
                                .help("ID for the type. Must be unique among ALL types and comply with the $INDICATOR_ID_PATTERN regular expression.")
                                .regex(INDICATOR_ID_PATTERN)
                                .readOnly(type != null)
                                .value(type?.id)
                )
                .with(
                        Selection.of(CreateTypeForm::category.name)
                                .label("Indicator category")
                                .help("The category the type is associated with.")
                                .items(indicatorCategoryService.findAll())
                                .itemId(IndicatorCategory::id.name)
                                .itemName(IndicatorCategory::name.name)
                                .value(type?.category?.id)
                )
                .with(
                        Text.of(CreateTypeForm::name.name)
                                .label("Name")
                                .help("Display name for the type.")
                                .value(type?.name)
                )
                .with(
                        Url.of(CreateTypeForm::link.name)
                                .label("Link to a longer description")
                                .optional()
                                .value(type?.link)
                )
                .with(
                        Text.of(CreateTypeForm::deprecated.name)
                                .label("Deprecation")
                                .help("If filled in, indicates that the type is deprecated and should not be used any longer.")
                                .optional()
                                .value(type?.deprecated)
                )
                .with(
                        ServiceConfigurator.of(CreateTypeForm::valueType.name)
                                .label("Value type")
                                .help("Type of value associated with this type.")
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