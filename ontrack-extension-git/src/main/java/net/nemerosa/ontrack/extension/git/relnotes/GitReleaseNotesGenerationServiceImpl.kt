package net.nemerosa.ontrack.extension.git.relnotes

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.api.model.IssueChangeLogExportRequest
import net.nemerosa.ontrack.extension.git.model.GitProjectNotConfiguredException
import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.extension.issues.export.ExportedIssues
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class GitReleaseNotesGenerationServiceImpl(
        private val gitService: GitService
) : GitReleaseNotesGenerationService {
    override fun changeLog(changeLogRequest: IssueChangeLogExportRequest): ExportedIssues? {
        // Gets the change log
        val changeLog = gitService.changeLog(changeLogRequest)
        // Gets the associated project
        val project: Project = changeLog.project
        // Gets the configuration for the project
        val gitConfiguration = gitService.getProjectConfiguration(project)
                ?: throw GitProjectNotConfiguredException(project.id)
        // Gets the issue service
        val configuredIssueService = gitConfiguration.configuredIssueService.getOrNull() ?: return null
        // Gets the issue change log
        val changeLogIssues = gitService.getChangeLogIssues(changeLog)
        // List of issues
        val issues = changeLogIssues.list.map { it.issue }
        // Exports the change log using the given format
        return configuredIssueService.issueServiceExtension
                .exportIssues(
                        configuredIssueService.issueServiceConfiguration,
                        issues,
                        changeLogRequest
                )
    }

}