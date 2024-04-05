package net.nemerosa.ontrack.extension.api

import net.nemerosa.ontrack.model.extension.Extension
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.Project

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

    /**
     * Inside a given [project], tries to find a build by using its display name or its name.
     *
     * If [onlyDisplayName] is `true`, only the display name will be used.
     */
    fun findBuildByDisplayName(
        project: Project,
        name: String,
        onlyDisplayName: Boolean,
    ): Build?

}