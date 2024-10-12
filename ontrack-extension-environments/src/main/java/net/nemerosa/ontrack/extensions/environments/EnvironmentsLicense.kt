package net.nemerosa.ontrack.extensions.environments

import net.nemerosa.ontrack.extension.license.control.LicenseControlService
import net.nemerosa.ontrack.extensions.environments.EnvironmentsLicensedFeatureProvider.Companion.FEATURE_ENVIRONMENTS

val LicenseControlService.environmentFeatureEnabled: Boolean get() = isFeatureEnabled(FEATURE_ENVIRONMENTS)

fun LicenseControlService.checkEnvironmentFeatureEnabled() {
    checkFeatureEnabled(FEATURE_ENVIRONMENTS)
}