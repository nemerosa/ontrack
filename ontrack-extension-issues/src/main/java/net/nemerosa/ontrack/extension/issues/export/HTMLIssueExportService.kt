package net.nemerosa.ontrack.extension.issues.export

import net.nemerosa.ontrack.extension.issues.IssueServiceExtension
import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration
import org.apache.commons.lang3.StringEscapeUtils.escapeHtml4
import org.springframework.stereotype.Component

@Component
class HTMLIssueExportService : AbstractTextIssueExportService(ExportFormat.HTML) {

    override fun exportAsText(issueServiceExtension: IssueServiceExtension, issueServiceConfiguration: IssueServiceConfiguration, groupedIssues: Map<String, List<Issue>>, s: StringBuilder) {
        groupedIssues.forEach { (groupName, issues) ->
            // One section per group
            s.append("""<section class="ontrack-issue-group">\n""")
            // Group header
            if (groupName.isNotBlank()) {
                s.append("""<hgroup><h1>${escapeHtml4(groupName)}</h1></hgroup>\n""")
            }
            // List of issues
            s.append(" <ul>\n")
            issues.forEach { issue ->
                s.append("""   <li>\n""")
                s.append("""      <a href="${issue.url}">${escapeHtml4(issue.displayKey)}</a> ${escapeHtml4(issue.summary)}\n""")
                s.append("""    </li>\n""")
            }
            s.append("  </ul>\n")
            // Closing the group section
            s.append("</section>\n")

        }
    }

    override fun exportSectionAsText(title: String, sectionType: SectionType, content: String): String {
        val className = when (sectionType) {
            SectionType.TITLE -> "ontrack-issue-title"
            SectionType.HEADING -> "ontrack-issue-heading"
        }
        return """
            <section class="$className">
                <hgroup><h1>$title</h1></hgroup>
                <div>$content</div>
            </section>
        """.trimIndent()
    }
}
