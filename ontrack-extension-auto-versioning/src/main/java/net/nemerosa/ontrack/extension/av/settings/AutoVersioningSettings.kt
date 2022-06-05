package net.nemerosa.ontrack.extension.av.settings

import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("Auto versioning settings")
data class AutoVersioningSettings(
    @APIDescription("""The "Auto versioning on promotion" feature is enabled only if this flag is set to `true`.""")
    val enabled: Boolean,
) {
    companion object {
        /**
         * Is the auto versioning enabled by default?
         */
        const val DEFAULT_ENABLED = false
    }
}
