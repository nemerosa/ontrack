package net.nemerosa.ontrack.model.ordering

import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.BranchNamePolicy

/**
 * Provides a way to order branches.
 */
interface BranchOrderingService {

    /**
     * Gets an ordering based on semantic versioning.
     *
     * The branch name or display name is used to get the semantic version
     * out of a pattern at its end.
     *
     * If not available, the branch is excluded from the ordering.
     *
     * @param branchNamePolicy How to get the branch name
     */
    fun getSemVerBranchOrdering(
        branchNamePolicy: BranchNamePolicy = BranchNamePolicy.DISPLAY_NAME_OR_NAME,
    ): Comparator<Branch>

    fun getRegexVersionComparator(regex: Regex): Comparator<Branch>

}