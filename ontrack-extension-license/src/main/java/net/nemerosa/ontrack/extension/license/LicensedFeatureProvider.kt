package net.nemerosa.ontrack.extension.license

interface LicensedFeatureProvider {

    val providedFeatures: List<ProvidedLicensedFeature>

}