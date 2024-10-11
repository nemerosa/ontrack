package net.nemerosa.ontrack.extension.license

import org.springframework.stereotype.Component

@Component
class CoreLicensedFeatureProvider : LicensedFeatureProvider {
    override val providedFeatures: List<ProvidedLicensedFeature> = listOf(
        ProvidedLicensedFeature("core", "Core features of Ontrack", alwaysEnabled = true),
    )
}