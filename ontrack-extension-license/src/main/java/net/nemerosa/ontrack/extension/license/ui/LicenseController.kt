package net.nemerosa.ontrack.extension.license.ui

import net.nemerosa.ontrack.extension.license.License
import net.nemerosa.ontrack.extension.license.LicenseService
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import net.nemerosa.ontrack.ui.resource.Resource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

/**
 * Gets access to the license.
 */
@RestController("/extension/license")
class LicenseController(
    private val licenseService: LicenseService,
) : AbstractResourceController() {

    @GetMapping("")
    fun getLicense(): Resource<License> =
        Resource.of(
            licenseService.license,
            uri(on(LicenseController::class.java).getLicense())
        )

}