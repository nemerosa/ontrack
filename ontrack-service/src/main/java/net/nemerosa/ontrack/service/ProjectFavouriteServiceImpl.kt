package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.model.security.ProjectView
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.ProjectFavouriteService
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.repository.ProjectFavouriteRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProjectFavouriteServiceImpl(
        private val repository: ProjectFavouriteRepository,
        private val securityService: SecurityService,
        private val structureService: StructureService
) : ProjectFavouriteService {

    override fun getFavouriteProjects(): List<Project> {
        val accountId = securityService.account.getOrNull()?.id()
        return if (accountId != null) {
            val projects = repository.getFavouriteProjects(accountId)
            projects.filter { securityService.isProjectFunctionGranted(it, ProjectView::class.java) }.map {
                structureService.getProject(ID.of(it))
            }
        } else {
            emptyList()
        }
    }

    override fun isProjectFavourite(project: Project): Boolean {
        val user = securityService.currentAccount
        return user?.let {
            it.isGranted(project.id(), ProjectView::class.java) && repository.isProjectFavourite(it.id(), project.id())
        } ?: false
    }

    override fun setProjectFavourite(project: Project, favourite: Boolean) {
        val user = securityService.currentAccount
        if (user != null) {
            repository.setProjectFavourite(user.id(), project.id(), favourite)
        }
    }

}