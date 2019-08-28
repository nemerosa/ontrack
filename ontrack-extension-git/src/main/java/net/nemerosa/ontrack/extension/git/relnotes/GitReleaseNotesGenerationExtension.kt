package net.nemerosa.ontrack.extension.git.relnotes

import net.nemerosa.ontrack.extension.api.model.IssueChangeLogExportRequest
import net.nemerosa.ontrack.extension.git.GitExtensionFeature
import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.extension.scm.relnotes.ReleaseNotesGenerationExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Component

/**
 * Generation of release notes for Git-based projects.
 */
@Component
class GitReleaseNotesGenerationExtension(
        private val gitService: GitService,
        private val gitReleaseNotesGenerationService: GitReleaseNotesGenerationService,
        extensionFeature: GitExtensionFeature
) : AbstractExtension(extensionFeature), ReleaseNotesGenerationExtension {

    /**
     * Checks if the project has a Git setup.
     */
    override fun appliesForProject(project: Project): Boolean = gitService.getProjectConfiguration(project) != null

    override fun changeLog(changeLogRequest: IssueChangeLogExportRequest): String? =
            gitReleaseNotesGenerationService.changeLog(changeLogRequest)?.content
}