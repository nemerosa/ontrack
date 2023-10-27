package net.nemerosa.ontrack.extension.av.dispatcher

import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.Project

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

    /**
     * Given a source project, an AV configuration FROM this project and an actual
     * stored [version], returns the corresponding build (if any).
     *
     * @param sourceProject Project where to look for the build
     * @param config Optional parameter for this configuration
     * @param version Actual version to look for. Its semantics depends on this version source.
     * @return Found build if any
     */
    fun getBuildFromVersion(sourceProject: Project, config: String?, version: String): Build?

}