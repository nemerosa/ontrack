package net.nemerosa.ontrack.service.ordering

import net.nemerosa.ontrack.common.Version
import net.nemerosa.ontrack.common.toVersion
import net.nemerosa.ontrack.model.ordering.BranchOrdering
import net.nemerosa.ontrack.model.structure.Branch
import org.springframework.stereotype.Component

/**
 * Branch ordering based on a [Version] extracted from the branch name or SCM path. Branches where a version
 * cannot be extracted are classified alphabetically and put at the end.
 */
@Component
class VersionBranchOrdering : BranchOrdering {

    override val id: String = "version"


    override fun getComparator(param: String?): Comparator<Branch> {
        return if (param != null && param.isNotBlank()) {
            val regex = param.toRegex()
            compareByDescending { it.getVersion(regex) }
        } else {
            throw IllegalArgumentException("`param` argument for the version branch ordering is required.")
        }
    }

    private fun Branch.getVersion(regex: Regex): Comparable<*> {
        // Path to use for the branch
        // TODO ... SCM path?
        val path: String = name
        // Extracts a version from the path
        val matcher = regex.matchEntire(path)
        if (matcher != null && matcher.groupValues.size >= 2) {
            // Getting the first group
            val token = matcher.groupValues[1]
            // Converting to a version
            val version = token.toVersion()
            // ... and using it if not null
            if (version != null) {
                return version
            }
        }
        // Name as default
        return name
    }
}