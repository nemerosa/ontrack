package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.model.pagination.PaginatedList

/**
 * Managing branches, replacement for [StructureService].
 */
interface BranchService {

    /**
     * Searching for branches by name fragment.
     */
    fun findBranchesByNamePattern(pattern: String, offset: Int, size: Int): PaginatedList<Branch>

}