package net.nemerosa.ontrack.extension.issues.export

import net.nemerosa.ontrack.extension.issues.IssueServiceExtension
import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration
import org.springframework.stereotype.Component

@Component
class TextIssueExportService implements IssueExportService {

    @Override
    ExportFormat getExportFormat() {
        ExportFormat.TEXT
    }

    @Override
    ExportedIssues export(IssueServiceExtension issueServiceExtension, IssueServiceConfiguration issueServiceConfiguration, Map<String, List<Issue>> groupedIssues) {
        StringBuilder s = new StringBuilder()

        groupedIssues.each { groupName, issues ->
            // Group header
            if (groupName) {
                s << "${groupName}\n\n"
            }
            // List of issues
            issues.each { issue ->
                s << "* ${issue.displayKey} ${issue.summary}\n"
            }
            // Group separator
            s << '\n'

        }

        new ExportedIssues(
                exportFormat.type,
                s.toString()
        )
    }
}
