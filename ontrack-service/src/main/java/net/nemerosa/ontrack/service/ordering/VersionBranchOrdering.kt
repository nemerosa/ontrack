package net.nemerosa.ontrack.service.ordering

import net.nemerosa.ontrack.common.Version
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
            compareBy { it.getVersion(param) }
        } else {
            throw IllegalArgumentException("`param` argument for the version branch ordering is required.")
        }
    }

    private fun Branch.getVersion(param: String): Comparable<*> {
        // Path to use for the branch
        // TODO ... SCM path?
        val path = name
    }
}