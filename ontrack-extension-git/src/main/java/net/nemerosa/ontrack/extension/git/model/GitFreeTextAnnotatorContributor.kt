package net.nemerosa.ontrack.extension.git.model

import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.support.FreeTextAnnotatorContributor
import net.nemerosa.ontrack.model.support.MessageAnnotator
import org.springframework.stereotype.Component

@Component
class GitFreeTextAnnotatorContributor(
        private val gitService: GitService
) : FreeTextAnnotatorContributor {
    override fun getMessageAnnotators(entity: ProjectEntity): List<MessageAnnotator> {
        val projectConfiguration: GitConfiguration? = gitService.getProjectConfiguration(entity.project)
        return listOfNotNull(
                projectConfiguration?.configuredIssueService
                        ?.flatMap { it.messageAnnotator }
                        ?.orElse(null)
        )
    }
}