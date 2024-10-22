package net.nemerosa.ontrack.extension.environments

import net.nemerosa.ontrack.extension.license.LicensedFeatureProvider
import net.nemerosa.ontrack.extension.license.ProvidedLicensedFeature
import org.springframework.stereotype.Component

@Component
class EnvironmentsLicensedFeatureProvider : LicensedFeatureProvider {

    override val providedFeatures: List<ProvidedLicensedFeature> = listOf(
        ProvidedLicensedFeature(
            id = FEATURE_ENVIRONMENTS,
            name = "Environments & deployment pipelines",
        )
    )

    companion object {
        const val FEATURE_ENVIRONMENTS = "extension.environments"
    }

}