package net.nemerosa.ontrack.extension.license.ui

import net.nemerosa.ontrack.extension.license.License
import net.nemerosa.ontrack.extension.license.LicenseService
import net.nemerosa.ontrack.extension.license.LicensedFeature
import net.nemerosa.ontrack.extension.license.control.LicenseControlService
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller

@Controller
class LicenseGraphQLController(
    private val licenseService: LicenseService,
    private val licenseControlService: LicenseControlService,
) {

    @QueryMapping
    fun licenseInfo(): LicenseResponse = licenseService.license
        .run {
            LicenseResponse(
                license = this,
                licenseControl = licenseControlService.control(this),
            )
        }

    @SchemaMapping
    fun licensedFeatures(license: License): List<LicensedFeature> =
        licenseControlService.getLicensedFeatures(license)

}