package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.ValidationRun
import net.nemerosa.ontrack.model.structure.ValidationRunSearchRequest
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID

interface ValidationRunSearchRepository {

    fun searchProjectValidationRuns(
            project: Project,
            request: ValidationRunSearchRequest,
            validationRunStatusService: (String) -> ValidationRunStatusID
    ): List<ValidationRun>

    fun totalProjectValidationRuns(
            project: Project,
            request: ValidationRunSearchRequest
    ): Int

}