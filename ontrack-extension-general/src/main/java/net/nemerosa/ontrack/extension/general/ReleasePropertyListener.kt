package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.model.structure.Build

/**
 * Listens to new release/label properties on builds.
 */
interface ReleasePropertyListener {

    /**
     * Triggers whenever the build has a new release/label
     */
    fun onBuildReleaseLabel(
        build: Build,
        releaseProperty: ReleaseProperty,
    )

}