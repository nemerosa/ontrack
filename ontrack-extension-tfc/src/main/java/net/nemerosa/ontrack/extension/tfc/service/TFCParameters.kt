package net.nemerosa.ontrack.extension.tfc.service

/**
 * Parameters used to identify a build validation.
 */
data class TFCParameters(
    val project: String,
    val branch: String,
    val build: String,
    val validation: String,
)