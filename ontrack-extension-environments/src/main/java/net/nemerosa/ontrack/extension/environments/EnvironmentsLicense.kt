package net.nemerosa.ontrack.extension.environments

import net.nemerosa.ontrack.extension.environments.EnvironmentsLicensedFeatureProvider.Companion.FEATURE_ENVIRONMENTS
import net.nemerosa.ontrack.extension.license.control.LicenseControlService
import net.nemerosa.ontrack.extension.license.control.LicenseFeatureException
import org.springframework.stereotype.Component

@Component
class EnvironmentsLicense(
    private val licenseControlService: LicenseControlService,
) {
    val environmentFeatureEnabled: Boolean by lazy {
        licenseControlService.isFeatureEnabled(FEATURE_ENVIRONMENTS)
    }

    fun checkEnvironmentFeatureEnabled() {
        if (!environmentFeatureEnabled) {
            throw LicenseFeatureException(FEATURE_ENVIRONMENTS)
        }
    }
}
