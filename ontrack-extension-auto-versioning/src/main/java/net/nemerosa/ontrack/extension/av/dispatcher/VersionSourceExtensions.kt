package net.nemerosa.ontrack.extension.av.dispatcher

import net.nemerosa.ontrack.extension.av.config.AutoVersioningSourceConfig
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.Project

/**
 * Given a `versionSource` parameter value, returns the ID of the version source
 * and its optional parameter.
 *
 * The expected format is `id(/config)`.
 *
 * For example:
 *
 * * `labelOnly`
 * * `metaInfo/key`
 */
fun getVersionSourceConfig(token: String?): Pair<String, String?> =
    if (token.isNullOrBlank()) {
        DefaultVersionSource.ID to null
    } else if (token.contains("/")) {
        token.substringBefore("/") to token.substringAfter("/")
    } else {
        token to null
    }

/**
 * Given a build and a version source expression, returns the build's version according to this expression.
 *
 * @receiver The [VersionSourceFactory] service
 * @param build The build for which to get a version
 * @param versionSource The version source expression
 * @return The build's version according to the AV configuration
 */
fun VersionSourceFactory.getBuildVersion(
    build: Build,
    versionSource: String?,
): String {
    val (id, param) = versionSource?.let {
        getVersionSourceConfig(it)
    } ?: (DefaultVersionSource.ID to null)
    return getVersionSource(id).getVersion(build, param)
}

/**
 * Given a build and an AV config, returns the build's version according to this configuration.
 *
 * @receiver The [VersionSourceFactory] service
 * @param build The build for which to get a version
 * @param config The auto versioning configuration to take into account
 * @return The build's version according to the AV configuration
 */
fun VersionSourceFactory.getBuildVersion(
    build: Build,
    config: AutoVersioningSourceConfig,
): String = getBuildVersion(build, config.versionSource)

/**
 * Given a source project and a version, use the `versionSource` ID to get
 * the corresponding build.
 */
fun VersionSourceFactory.getBuildWithVersion(
    sourceProject: Project,
    versionSource: String?,
    version: String
): Build? {
    val (id, param) = versionSource?.let {
        getVersionSourceConfig(it)
    } ?: (DefaultVersionSource.ID to null)
    return getVersionSource(id).getBuildFromVersion(sourceProject, param, version)
}

/**
 * Given a source project and a version, use the `versionSource` ID to get
 * the corresponding build.
 */
fun VersionSourceFactory.getBuildWithVersion(
    sourceProject: Project,
    config: AutoVersioningSourceConfig,
    version: String
): Build? = getBuildWithVersion(sourceProject, config.versionSource, version)
