package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.ValidationStamp

interface ValidationStampRepository {

    fun findByToken(token: String): List<ValidationStamp>

    fun findBranchesWithValidationStamp(project: Project, validation: String): List<Branch>

}