package net.nemerosa.ontrack.extension.license.embedded

import net.nemerosa.ontrack.extension.license.signature.AbstractSignatureLicenseService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service


@Service
@ConditionalOnProperty(
    name = ["ontrack.config.license.provider"],
    havingValue = "embedded",
    matchIfMissing = false
)
class EmbeddedLicenseService(
    embeddedLicenseConfigurationProperties: EmbeddedLicenseConfigurationProperties,
) : AbstractSignatureLicenseService() {

    override val licenseType: String = "Embedded"
    override val encodedLicense: String? = embeddedLicenseConfigurationProperties.key
}