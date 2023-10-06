package net.nemerosa.ontrack.extension.api

import net.nemerosa.ontrack.model.extension.Extension
import net.nemerosa.ontrack.model.structure.Build

/**
 * Extension which allows to render the display name for
 * a [Build].
 */
interface BuildDisplayNameExtension : Extension {

    /**
     * Returns a name according to the build configuration or null if not available.
     */
    fun getBuildDisplayName(build: Build): String?

    /**
     * Checks if the build MUST return a name according to its configuration.
     */
    fun mustProvideBuildName(build: Build): Boolean

}