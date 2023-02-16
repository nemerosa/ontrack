package net.nemerosa.ontrack.extension.issues.export

import net.nemerosa.ontrack.extension.issues.IssueServiceExtension
import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration
import org.springframework.stereotype.Component

@Component
class SlackIssueExportService : AbstractTextIssueExportService(ExportFormat.SLACK) {

    override fun exportAsText(
        issueServiceExtension: IssueServiceExtension,
        issueServiceConfiguration: IssueServiceConfiguration,
        groupedIssues: Map<String, List<Issue>>,
        s: StringBuilder,
    ) {
        groupedIssues.forEach { (groupName, issues) ->
            // Group header
            if (groupName.isNotBlank()) {
                s.append("$groupName\n\n")
            }
            // List of issues
            issues.forEach { issue ->
                s.append("* <${issue.url}|${issue.displayKey}> ${issue.summary}\n")
            }
            // Group separator
            s.append("\n")
        }
    }
}
