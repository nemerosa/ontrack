package net.nemerosa.ontrack.extension.git.resource

import net.nemerosa.ontrack.extension.api.model.IssueChangeLogExportRequest
import net.nemerosa.ontrack.extension.git.GitController
import net.nemerosa.ontrack.extension.git.model.GitChangeLog
import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.extension.scm.SCMController
import net.nemerosa.ontrack.ui.resource.AbstractLinkResourceDecorator
import net.nemerosa.ontrack.ui.resource.LinkDefinition
import net.nemerosa.ontrack.ui.resource.linkIf
import net.nemerosa.ontrack.ui.resource.linkTo
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@Component
class GitChangeLogResourceDecorator(
    private val gitService: GitService
) : AbstractLinkResourceDecorator<GitChangeLog>(GitChangeLog::class.java) {

    override fun getLinkDefinitions(): Iterable<LinkDefinition<GitChangeLog>> = listOf(
        // Commits
        "_commits" linkTo { changeLog ->
            on(GitController::class.java).changeLogCommits(changeLog.uuid, null)
        },
        // Issues
        "_issues" linkTo { changeLog: GitChangeLog, _ ->
            on(GitController::class.java).changeLogIssues(changeLog.uuid)
        } linkIf { changeLog, _ ->
            hasIssues(changeLog)
        },
        "_issuesIds" linkTo { changeLog: GitChangeLog, _ ->
            on(GitController::class.java).changeLogIssuesIds(changeLog.uuid)
        } linkIf { changeLog, _ ->
            hasIssues(changeLog)
        },
        // Files
        "_files" linkTo { changeLog ->
            on(GitController::class.java).changeLogFiles(changeLog.uuid)
        },
        // Change log filters
        "_changeLogFileFilters" linkTo { changeLog ->
            on(SCMController::class.java).getChangeLogFileFilters(changeLog.project.id)
        },
        // Diff
        "_diff" linkTo { _ ->
            on(GitController::class.java).diff(null)
        },
        // Export formats
        "_exportFormats" linkTo { changeLog ->
            on(GitController::class.java).changeLogExportFormats(changeLog.project.id)
        },
        // Export of issues
        "_exportIssues" linkTo { changeLog ->
            on(GitController::class.java).changeLog(IssueChangeLogExportRequest())
        },
    )

    private fun hasIssues(changeLog: GitChangeLog) =
        gitService.getProjectConfiguration(changeLog.project) != null

}