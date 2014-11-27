package net.nemerosa.ontrack.extension.issues.export

import net.nemerosa.ontrack.extension.issues.IssueServiceExtension
import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration
import org.springframework.stereotype.Component

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4

@Component
class HTMLIssueExportService extends AbstractTextIssueExportService {

    HTMLIssueExportService() {
        super(ExportFormat.HTML)
    }

    @Override
    void exportAsText(IssueServiceExtension issueServiceExtension, IssueServiceConfiguration issueServiceConfiguration, Map<String, List<Issue>> groupedIssues, StringBuilder s) {
        groupedIssues.each { groupName, issues ->
            // One section per group
            s << '<section class="ontrack-issue-group">\n'
            // Group header
            if (groupName) {
                s << "  <hgroup><h1>${escapeHtml4(groupName)}</h1></hgroup>\n"
            }
            // List of issues
            s << '  <ul>\n'
            issues.each { issue ->
                s << "    <li>\n"
                s << """      <a href="${issue.url}">${escapeHtml4(issue.displayKey)}</a> ${escapeHtml4(issue.summary)}\n"""
                s << "    </li>\n"
            }
            s << '  </ul>\n'
            // Closing the group section
            s << '</section>\n'

        }
    }
}
