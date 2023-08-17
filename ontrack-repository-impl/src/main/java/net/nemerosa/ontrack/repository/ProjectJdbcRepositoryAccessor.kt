package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.Project

interface ProjectJdbcRepositoryAccessor {

    fun getProject(id: ID): Project

}