package net.nemerosa.ontrack.extension.environments

import net.nemerosa.ontrack.extension.environments.EnvironmentsLicensedFeatureProvider.Companion.FEATURE_ENVIRONMENTS
import net.nemerosa.ontrack.extension.license.control.LicenseControlService
import net.nemerosa.ontrack.extension.license.control.LicenseFeatureDataException
import net.nemerosa.ontrack.extension.license.control.LicenseFeatureException
import net.nemerosa.ontrack.extension.license.control.parseLicenseData
import org.springframework.stereotype.Component

@Component
class EnvironmentsLicense(
    private val licenseControlService: LicenseControlService,
) {

    val environmentFeatureEnabled: Boolean by lazy {
        licenseControlService.isFeatureEnabled(FEATURE_ENVIRONMENTS)
    }

    private val environmentsLicenseData: EnvironmentsLicenseData by lazy {
        licenseControlService.parseLicenseData<EnvironmentsLicenseData>(FEATURE_ENVIRONMENTS)
            ?: EnvironmentsLicenseData(
                maxEnvironments = 0, // Unlimited when no license
            )
    }

    val maxEnvironments: Int get() = environmentsLicenseData.maxEnvironments

    fun checkEnvironmentFeatureEnabled() {
        if (!environmentFeatureEnabled) {
            throw LicenseFeatureException(FEATURE_ENVIRONMENTS)
        }
    }

    fun maxEnvironmentsReached() {
        throw LicenseFeatureDataException(FEATURE_ENVIRONMENTS, "Maximum number of environments reached")
    }
}
