package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.extension.indicators.acl.IndicatorTypeManagement
import net.nemerosa.ontrack.extension.indicators.model.IndicatorTypeService
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import net.nemerosa.ontrack.ui.resource.Link
import net.nemerosa.ontrack.ui.resource.Resources
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

/**
 * Controller for the management of types.
 */
@RestController
@RequestMapping("/extension/indicators/types")
class IndicatorTypeController(
        private val indicatorTypeService: IndicatorTypeService,
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
    fun getCreationForm(): Form = TODO()

}