package net.nemerosa.ontrack.model.ordering

/**
 * Provides a way to order branches.
 */
interface BranchOrderingService {

    /**
     * Given an [id], returns a [BranchOrdering].
     */
    fun getBranchOrdering(id: String): BranchOrdering?

    /**
     * Gets the list of available branch orderings.
     */
    val branchOrderings: List<BranchOrdering>

}