package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.model.security.ProjectView
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.BranchFavouriteService
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.repository.BranchFavouriteRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BranchFavouriteServiceImpl(
        private val repository: BranchFavouriteRepository,
        private val securityService: SecurityService,
        private val structureService: StructureService
) : BranchFavouriteService {

    override fun getFavouriteBranches(): List<Branch> {
        val accountId = securityService.currentAccount?.account?.id()
        return if (accountId != null) {
            val branches = securityService.asAdmin {
                repository.getFavouriteBranches(accountId)
                        .map { id -> structureService.getBranch(ID.of(id)) }
            }
            branches.filter { securityService.isProjectFunctionGranted(it, ProjectView::class.java) }
        } else {
            emptyList()
        }
    }

    override fun isBranchFavourite(branch: Branch): Boolean {
        val user = securityService.currentAccount
        return user != null &&
                user.isGranted(branch.projectId(), ProjectView::class.java) &&
                repository.isBranchFavourite(user.account.id(), branch.id())
    }

    override fun setBranchFavourite(branch: Branch, favourite: Boolean) {
        val user = securityService.currentAccount
        if (user != null && user.isGranted(branch.projectId(), ProjectView::class.java)) {
            repository.setBranchFavourite(user.account.id(), branch.id(), favourite)
        }
    }
}
