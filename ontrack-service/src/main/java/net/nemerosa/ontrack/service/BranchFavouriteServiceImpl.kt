package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.model.security.ProjectView
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.security.callAsAdmin
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
        val accountId = securityService.account.getOrNull()?.id()
        return if (accountId != null) {
            val branches = securityService.callAsAdmin {
                repository.getFavouriteBranches(accountId)
                        .map { id -> structureService.getBranch(ID.of(id)) }
            }
            branches.filter { securityService.isProjectFunctionGranted(it, ProjectView::class.java) }
        } else {
            emptyList()
        }
    }

    override fun isBranchFavourite(branch: Branch): Boolean {
        return securityService.isProjectFunctionGranted(branch, ProjectView::class.java) && securityService.account.filter { account -> account.id.isSet }
                .map { account ->
                    repository.isBranchFavourite(
                            account.id(),
                            branch.id()
                    )
                }.orElse(false)
    }

    override fun setBranchFavourite(branch: Branch, favourite: Boolean) {
        if (securityService.isProjectFunctionGranted(branch, ProjectView::class.java)) {
            securityService.account.ifPresent { account -> repository.setBranchFavourite(account.id(), branch.id(), favourite) }
        }
    }
}
