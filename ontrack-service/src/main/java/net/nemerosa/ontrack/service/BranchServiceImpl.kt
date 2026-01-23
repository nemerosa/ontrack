package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.model.pagination.PaginatedList
import net.nemerosa.ontrack.model.security.ProjectView
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.BranchService
import net.nemerosa.ontrack.repository.BranchRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BranchServiceImpl(
    private val branchRepository: BranchRepository,
    private val securityService: SecurityService,
) : BranchService {

    override fun findBranchesByNamePattern(
        pattern: String,
        offset: Int,
        size: Int
    ): PaginatedList<Branch> =
        branchRepository.findBranchesByNamePattern(pattern, offset, size)
            .filter {
                securityService.isProjectFunctionGranted(it, ProjectView::class.java)
            }

}