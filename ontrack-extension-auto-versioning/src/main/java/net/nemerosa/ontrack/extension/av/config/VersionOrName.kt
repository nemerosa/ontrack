package net.nemerosa.ontrack.extension.av.config

import net.nemerosa.ontrack.common.Version

class VersionOrName private constructor(
        val version: Version?,
        val name: String?
) : Comparable<VersionOrName> {

    constructor(version: Version) : this(version, null)
    constructor(id: String) : this(null, id)

    override fun compareTo(other: VersionOrName): Int {
        // Name does not matter when having a version
        return if (version != null && other.version != null) {
            version.compareTo(other.version)
        }
        // Version supersedes any name
        else if (version != null) {
            1
        }
        // Version supersedes any name
        else if (other.version != null) {
            -1
        }
        // Comparison on name
        else if (name != null && other.name != null) {
            name.compareTo(other.name)
        }
        // ID cannot be null, but just in case
        else {
            (name ?: "").compareTo(other.name ?: "")
        }
    }
}