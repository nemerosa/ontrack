package net.nemerosa.ontrack.extension.av.dispatcher

import net.nemerosa.ontrack.model.structure.Build

/**
 * Defines how to compute the version of a given [build][Build].
 */
interface VersionSource {

    /**
     * ID used to identify this source
     */
    val id: String

    /**
     * Given a build and some optional parameter, computes the version of this build.
     *
     * @param build Build from which to get the version
     * @param config Optional parameter for this configuration
     * @return The build version. Never null.
     * @throws VersionSourceNoVersionException When no version can be extracted from the build
     */
    fun getVersion(build: Build, config: String?): String

}