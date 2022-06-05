package net.nemerosa.ontrack.extension.git.service

import net.nemerosa.ontrack.extension.scm.service.SCMService
import net.nemerosa.ontrack.extension.scm.service.SCMServiceProvider
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Component
import java.util.*

@Component
class GitServiceProvider(
    private val gitService: GitService,
) : SCMServiceProvider {

    override fun getScmService(branch: Branch): Optional<SCMService> = getScmService(branch.project)

    override fun getScmService(project: Project): Optional<SCMService> {
        val projectConfiguration = gitService.getProjectConfiguration(project)
        return if (projectConfiguration != null) {
            Optional.of(gitService)
        } else {
            Optional.empty()
        }
    }

    override fun getProjectScmService(project: Project): SCMService? {
        val projectConfiguration = gitService.getProjectConfiguration(project)
        return if (projectConfiguration != null) {
            gitService
        } else {
            null
        }
    }
}