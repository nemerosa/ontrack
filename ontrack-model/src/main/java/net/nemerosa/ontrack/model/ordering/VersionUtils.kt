package net.nemerosa.ontrack.model.ordering

import net.nemerosa.ontrack.common.toVersion

object VersionUtils {
    
    fun getVersion(regex: Regex, path: String): VersionOrName? {
        val matcher = regex.matchEntire(path)
        return if (matcher != null) {
            // There is at least one capturing group, we can use it
            if (matcher.groupValues.size >= 2) {
                // Getting the first group
                val token = matcher.groupValues[1]
                // Converting to a version
                toVersion(token)
            } else {
                // There is no capturing group
                // We just take any text behind "/"
                val index = path.indexOf("/")
                if (index >= 0) {
                    // Getting the first group
                    val token = path.substring(index + 1)
                    // Converting to a version
                    toVersion(token)
                } else {
                    null
                }
            }
        } else {
            // No match at all
            null
        }
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