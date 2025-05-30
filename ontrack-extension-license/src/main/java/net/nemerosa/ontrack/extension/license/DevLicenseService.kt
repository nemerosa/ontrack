package net.nemerosa.ontrack.extension.license

import net.nemerosa.ontrack.common.RunProfile
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile(RunProfile.DEV)
class DevLicenseService(
    licensedFeatureProviders: List<LicensedFeatureProvider>,
) : LicenseService {

    private val features = licensedFeatureProviders
        .flatMap { it.providedFeatures }
        .map {
            LicenseFeatureData(
                id = it.id,
                enabled = true,
                data = emptyList(),
            )
        }

    override val license: License = License(
        type = "dev",
        name = "Development license",
        assignee = "Development",
        maxProjects = 0,
        active = true,
        validUntil = null,
        features = features,
        message = "You're currently using a development license."
    )
}