package net.nemerosa.ontrack.extension.config.license

import net.nemerosa.ontrack.extension.license.LicensedFeatureProvider
import net.nemerosa.ontrack.extension.license.ProvidedLicensedFeature
import org.springframework.stereotype.Component

@Component
class ConfigurationLicensedFeatureProvider : LicensedFeatureProvider {

    override val providedFeatures: List<ProvidedLicensedFeature> = listOf(
        ProvidedLicensedFeature(
            id = FEATURE_CONFIGURATION,
            name = "Injection of configuration from the CI",
        )
    )

    companion object {
        const val FEATURE_CONFIGURATION = "extension.configuration"
    }

}