package net.nemerosa.ontrack.kdsl.spec.extension.av

/**
 * Configuration for an additional path in an auto-versioning configuration.
 */
data class AutoVersioningSourceConfigPath(
    val path: String,
    val regex: String? = null,
    val property: String? = null,
    val propertyRegex: String? = null,
    val propertyType: String? = null,
    val versionSource: String? = null,
)
