package net.nemerosa.ontrack.extension.issues.export

import net.nemerosa.ontrack.extension.issues.IssueServiceExtension
import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration
import org.springframework.stereotype.Component

@Component
class MarkdownIssueExportService : AbstractTextIssueExportService(ExportFormat.MARKDOWN) {

    override fun exportAsText(issueServiceExtension: IssueServiceExtension, issueServiceConfiguration: IssueServiceConfiguration, groupedIssues: Map<String, List<Issue>>, s: StringBuilder) {
        groupedIssues.forEach { (groupName, issues) ->
            // Group header
            if (groupName.isNotBlank()) {
                s.append("### $groupName\n\n")
            }
            // List of issues
            issues.forEach { issue ->
                s.append("* [${issue.displayKey}](${issue.url}) ${issue.summary}\n")
            }
            // Group separator
            s.append('\n')

        }
    }

    override fun exportSectionAsText(title: String?, sectionType: SectionType, content: String): String =
            sectionAsText(title, sectionType, content)

    companion object {
        fun sectionAsText(title: String?, sectionType: SectionType, content: String): String {
            val s = StringBuilder()
            if (title != null) {
                when (sectionType) {
                    SectionType.TITLE -> {
                        s.append("$title\n")
                        s.append("=".repeat(title.length)).append("\n")
                        s.append("\n")
                    }
                    SectionType.HEADING -> {
                        s.append("## $title\n")
                        s.append("\n")
                    }
                }
            }
            s.append(content)
            return s.toString()
        }
    }

}
