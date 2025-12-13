package net.nemerosa.ontrack.extension.license

import net.nemerosa.ontrack.common.RunProfile
import net.nemerosa.ontrack.extension.license.signature.AbstractSignatureLicenseService
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile(RunProfile.PROD)
class ProductionLicenseService(
    licenseConfigurationProperties: LicenseConfigurationProperties,
    licenseKeyPath: String = "/keys/embedded.key",
) : AbstractSignatureLicenseService(licenseKeyPath) {

    override val licenseType: String = "Production"
    override val encodedLicense: String? = licenseConfigurationProperties.key
}