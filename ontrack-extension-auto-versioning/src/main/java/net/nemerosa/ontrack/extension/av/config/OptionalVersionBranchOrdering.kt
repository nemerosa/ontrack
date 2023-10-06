package net.nemerosa.ontrack.extension.av.config

import net.nemerosa.ontrack.common.Version
import net.nemerosa.ontrack.common.toVersion
import net.nemerosa.ontrack.model.ordering.BranchOrdering
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.BranchDisplayNameService
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
        val path: String = branchDisplayNameService.getBranchDisplayName(branch)
        // Path first, then name, then version = name
        return getVersion(regex, path) ?: getVersion(regex, branch.name) ?: VersionOrName(path)
    }

    internal fun getVersion(regex: Regex, path: String): VersionOrName? {
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
    internal fun toVersion(token: String): VersionOrName? {
        // Converting to a version
        val version = token.toVersion()
        // ... and using it if not null
        return version?.let { VersionOrName(it) }
    }

    companion object {
        /**
         * ID of this [BranchOrdering]
         */
        const val OPTIONAL_VERSION_BRANCH_ORDERING_ID = "optional-version"
    }

}