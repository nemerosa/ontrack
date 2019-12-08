package net.nemerosa.ontrack.extension.git.service

import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService
import net.nemerosa.ontrack.extension.issues.support.IssueServiceExtensionContributor
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Component

@Component
class GitIssueServiceExtensionContributor(
        private val gitService: GitService
) : IssueServiceExtensionContributor {
    override fun getIssueServiceExtension(project: Project): ConfiguredIssueService? =
            gitService.getProjectConfiguration(project)
                    ?.configuredIssueService
                    ?.orElse(null)
}