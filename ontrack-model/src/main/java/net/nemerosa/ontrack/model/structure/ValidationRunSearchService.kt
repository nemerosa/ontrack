package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.model.pagination.PaginatedList

/**
 * Service used to search for validation runs.
 */
interface ValidationRunSearchService {

    fun searchProjectValidationRuns(project: Project, request: ValidationRunSearchRequest): PaginatedList<ValidationRun>

}