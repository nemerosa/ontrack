package net.nemerosa.ontrack.extension.av.dispatcher

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
