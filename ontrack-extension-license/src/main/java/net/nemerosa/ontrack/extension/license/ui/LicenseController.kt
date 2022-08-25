package net.nemerosa.ontrack.extension.license.ui

import net.nemerosa.ontrack.extension.license.LicenseService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Gets access to the license.
 */
@RestController
@RequestMapping("/extension/license")
class LicenseController(
    private val licenseService: LicenseService,
) {

    @GetMapping("")
    fun getLicense(): LicenseResponse? = LicenseResponse(licenseService.license)

}