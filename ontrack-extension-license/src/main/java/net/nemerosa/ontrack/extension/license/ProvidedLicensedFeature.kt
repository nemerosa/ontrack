package net.nemerosa.ontrack.extension.license

data class ProvidedLicensedFeature(
    val id: String,
    val name: String,
    val alwaysEnabled: Boolean = false,
)
