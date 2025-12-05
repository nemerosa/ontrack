package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.Project

interface BranchJdbcRepositoryAccessor {

    fun getBranch(id: ID, branch: Project? = null): Branch

}