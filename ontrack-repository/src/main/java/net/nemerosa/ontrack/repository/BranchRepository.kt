package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.BranchFilter
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.Project

/**
 * Specific queries for branches.
 */
interface BranchRepository {

    /**
     * Returns a list of branches for a project, according to a provided filter.
     *
     * @param project Project to get branches for
     * @param user ID of the current authenticated user (needed for [BranchFilter.favorite])
     * @param filter Branch filter to use
     */
    fun filterBranchesForProject(project: Project, user: ID?, filter: BranchFilter): List<Branch>
}