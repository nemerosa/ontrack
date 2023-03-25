package net.nemerosa.ontrack.extension.tfc.hook

/**
 * Parameters expected from the hook, present in the URL. All are required.
 */
data class TFCHookParameters(
    val project: String,
    val branch: String,
    val build: String,
    val validation: String,
)
