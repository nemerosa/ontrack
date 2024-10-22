package net.nemerosa.ontrack.extension.environments

import net.nemerosa.ontrack.extension.environments.EnvironmentsLicensedFeatureProvider.Companion.FEATURE_ENVIRONMENTS
import net.nemerosa.ontrack.extension.license.control.LicenseControlService

val LicenseControlService.environmentFeatureEnabled: Boolean get() = isFeatureEnabled(FEATURE_ENVIRONMENTS)

fun LicenseControlService.checkEnvironmentFeatureEnabled() {
    checkFeatureEnabled(FEATURE_ENVIRONMENTS)
}