package net.nemerosa.ontrack.extension.tfc.hook

import net.nemerosa.ontrack.extension.tfc.service.TFCParameters

/**
 * Parameters expected from the hook, present in the URL. All are required.
 */
data class TFCHookParameters(
    val project: String,
    val branch: String,
    val build: String,
    val validation: String,
) {
    fun toServiceParameters() = TFCParameters(
        project = project,
        branch = branch,
        build = build,
        validation = validation,
    )
}
