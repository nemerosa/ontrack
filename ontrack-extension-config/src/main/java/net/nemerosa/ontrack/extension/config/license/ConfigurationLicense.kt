package net.nemerosa.ontrack.extension.config.license

import net.nemerosa.ontrack.extension.config.license.ConfigurationLicensedFeatureProvider.Companion.FEATURE_CONFIGURATION
import net.nemerosa.ontrack.extension.license.control.LicenseControlService
import net.nemerosa.ontrack.extension.license.control.LicenseFeatureException
import org.springframework.stereotype.Component

@Component
class ConfigurationLicense(
    private val licenseControlService: LicenseControlService,
) {

    val configurationFeatureEnabled: Boolean by lazy {
        licenseControlService.isFeatureEnabled(FEATURE_CONFIGURATION)
    }

    fun checkConfigurationFeatureEnabled() {
        if (!configurationFeatureEnabled) {
            throw LicenseFeatureException(FEATURE_CONFIGURATION)
        }
    }

}
