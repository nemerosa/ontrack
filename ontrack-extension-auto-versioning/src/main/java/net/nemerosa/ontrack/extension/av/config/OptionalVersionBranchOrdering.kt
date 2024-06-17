package net.nemerosa.ontrack.extension.av.config

import net.nemerosa.ontrack.common.Version
import net.nemerosa.ontrack.model.ordering.BranchOrdering
import net.nemerosa.ontrack.model.ordering.VersionOrName
import net.nemerosa.ontrack.model.ordering.VersionUtils
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.BranchDisplayNameService
import net.nemerosa.ontrack.model.structure.BranchNamePolicy
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

/**
 * [BranchOrdering] based on a [Version] in a regex capturing group
 * or if no capturing group is specified, on the text after the first `/` separator.
 *
 * When no version can be checked, it falls back on ordering on on the [ID][Branch.id] of the branch.
 *
 * It is loosely based on the `version` [BranchOrdering] which requires
 * a capturing group.
 */
@Component
@Qualifier("optionalVersion")
class OptionalVersionBranchOrdering(
    private val branchDisplayNameService: BranchDisplayNameService
) : BranchOrdering {

    override val id: String = OPTIONAL_VERSION_BRANCH_ORDERING_ID

    override fun getComparator(param: String?): Comparator<Branch> {
        return if (!param.isNullOrBlank()) {
            val regex = param.toRegex()
            compareByDescending { branch -> getVersion(branch, regex) }
        } else {
            throw IllegalArgumentException("`param` argument for the version branch ordering is required.")
        }
    }

    internal fun getVersion(branch: Branch, regex: Regex): VersionOrName {
        // Path to use for the branch
        val path: String = branchDisplayNameService.getBranchDisplayName(branch, BranchNamePolicy.DISPLAY_NAME_OR_NAME)
        // Path first, then name, then version = name
        return VersionUtils.getVersion(regex, path) ?: VersionOrName(path)
    }

    companion object {
        /**
         * ID of this [BranchOrdering]
         */
        const val OPTIONAL_VERSION_BRANCH_ORDERING_ID = "optional-version"
    }

}