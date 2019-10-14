package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.model.pagination.PaginatedList
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.repository.ValidationRunSearchRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ValidationRunSearchServiceImpl(
        private val validationRunSearchRepository: ValidationRunSearchRepository,
        private val validationRunStatusService: ValidationRunStatusService
) : ValidationRunSearchService {

    override fun searchProjectValidationRuns(project: Project, request: ValidationRunSearchRequest): PaginatedList<ValidationRun> {
        // Items being found
        val list = validationRunSearchRepository.searchProjectValidationRuns(
                project,
                request
        ) {
            validationRunStatusService.getValidationRunStatus(it)
        }
        // Total of items
        val total = validationRunSearchRepository.totalProjectValidationRuns(
                project,
                request
        )
        // Result
        return PaginatedList.create(
                list,
                request.offset,
                request.size,
                total
        )
    }

}