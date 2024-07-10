package net.nemerosa.ontrack.model.ordering

import net.nemerosa.ontrack.common.toVersion

object VersionUtils {

    /**
     * Regex used to get a version from the end of a name.
     */
    val semVerSuffixRegex = "(\\d+(\\.\\d+)+)\$".toRegex()

    fun getVersionText(regex: Regex, path: String): String? {
        val matcher = regex.find(path)
        return if (matcher != null) {
            // There is at least one capturing group, we can use it
            if (matcher.groupValues.size >= 2) {
                // Getting the first group
                matcher.groupValues[1]
            } else {
                // There is no capturing group
                // We just take any text behind "/"
                val index = path.indexOf("/")
                if (index >= 0) {
                    // Getting the first group
                    path.substring(index + 1)
                } else {
                    null
                }
            }
        } else {
            // No match at all
            null
        }
    }

    fun getVersion(regex: Regex, path: String): VersionOrName? {
        val text = getVersionText(regex, path)
        return text?.let { toVersion(it) }
    }

    /**
     * Conversion of a token to a version when possible
     */
    fun toVersion(token: String): VersionOrName? {
        // Converting to a version
        val version = token.toVersion()
        // ... and using it if not null
        return version?.let { VersionOrName(it) }
    }

}