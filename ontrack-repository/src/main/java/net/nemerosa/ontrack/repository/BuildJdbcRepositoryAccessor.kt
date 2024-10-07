package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.ID

interface BuildJdbcRepositoryAccessor {

    fun getBuild(id: ID, branch: Branch? = null): Build

}