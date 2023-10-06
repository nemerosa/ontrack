package net.nemerosa.ontrack.extension.av.dispatcher

import net.nemerosa.ontrack.extension.av.config.AutoVersioningSourceConfig
import net.nemerosa.ontrack.model.structure.Build

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
fun getVersionSourceConfig(token: String): Pair<String, String?> =
    if (token.contains("/")) {
        token.substringBefore("/") to token.substringAfter("/")
    } else {
        token to null
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
): String {
    val (id, param) = config.versionSource?.let {
        getVersionSourceConfig(it)
    } ?: (DefaultVersionSource.ID to null)
    return getVersionSource(id).getVersion(build, param)
}
