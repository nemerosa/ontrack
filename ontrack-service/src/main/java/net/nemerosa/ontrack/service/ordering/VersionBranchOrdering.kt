package net.nemerosa.ontrack.service.ordering

import net.nemerosa.ontrack.common.Version
import net.nemerosa.ontrack.common.toVersion
import net.nemerosa.ontrack.model.ordering.BranchOrdering
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.BranchDisplayNameService
import org.springframework.stereotype.Component

/**
 * Branch ordering based on a [Version] extracted from the branch name or SCM path. Branches where a version
 * cannot be extracted are classified alphabetically and put at the end.
 */
@Component
class VersionBranchOrdering(
        val branchDisplayNameService: BranchDisplayNameService
) : BranchOrdering {

    override val id: String = "version"

    override fun getComparator(param: String?): Comparator<Branch> {
        return if (param != null && param.isNotBlank()) {
            val regex = param.toRegex()
            compareByDescending { it.getVersion(regex) }
        } else {
            throw IllegalArgumentException("`param` argument for the version branch ordering is required.")
        }
    }

    private fun Branch.getVersion(regex: Regex): VersionOrString {
        // Path to use for the branch
        val path: String = branchDisplayNameService.getBranchDisplayName(this)
        // Path first, then name, then version = name
        return getVersion(regex, path) ?: getVersion(regex, name) ?: VersionOrString(null)
    }

    private fun getVersion(regex: Regex, path: String): VersionOrString? {
        val matcher = regex.matchEntire(path)
        if (matcher != null && matcher.groupValues.size >= 2) {
            // Getting the first group
            val token = matcher.groupValues[1]
            // Converting to a version
            val version = token.toVersion()
            // ... and using it if not null
            if (version != null) {
                return VersionOrString(version)
            }
        }
        // Nothing
        return null
    }

    private class VersionOrString(
            val version: Version?
    ) : Comparable<VersionOrString> {
        override fun compareTo(other: VersionOrString): Int {
            return if (version != null && other.version != null) {
                version.compareTo(other.version)
            } else if (version != null) {
                1
            } else {
                -1
            }
        }
    }
}