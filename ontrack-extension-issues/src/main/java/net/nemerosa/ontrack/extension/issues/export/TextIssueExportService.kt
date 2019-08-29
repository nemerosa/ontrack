package net.nemerosa.ontrack.extension.issues.export

import net.nemerosa.ontrack.extension.issues.IssueServiceExtension
import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration
import org.springframework.stereotype.Component

@Component
class TextIssueExportService : AbstractTextIssueExportService(ExportFormat.TEXT) {

    override fun exportAsText(issueServiceExtension: IssueServiceExtension, issueServiceConfiguration: IssueServiceConfiguration, groupedIssues: Map<String, List<Issue>>, s: StringBuilder) {
        groupedIssues.forEach { (groupName, issues) ->
            // Group header
            if (groupName.isNotBlank()) {
                s.append("$groupName\n\n")
            }
            // List of issues
            issues.forEach { issue ->
                s.append("* ${issue.displayKey} ${issue.summary}\n")
            }
            // Group separator
            s.append('\n')
        }
    }

    @Suppress("DuplicatedCode")
    override fun exportSectionAsText(title: String?, sectionType: SectionType, content: String): String =
            if (title != null) {
                when (sectionType) {
                    SectionType.TITLE -> """
                        $title
                        ${"=".repeat(title.length)}
                        
                        $content
                        """.trimIndent()
                    SectionType.HEADING -> """
                        ## $title
                        
                        $content
                        """.trimIndent()
                }
            } else {
                content
            }
}
