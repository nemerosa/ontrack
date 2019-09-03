package net.nemerosa.ontrack.model.ordering

import net.nemerosa.ontrack.model.structure.Branch

/**
 * Defines a way to order branches.
 */
interface BranchOrdering {

    /**
     * ID of this ordering
     */
    val id: String

    /**
     * Comparator for branches
     */
    fun getComparator(param: String?): Comparator<Branch>

}