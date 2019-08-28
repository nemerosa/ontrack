package net.nemerosa.ontrack.extension.issues.export

import net.nemerosa.ontrack.extension.issues.IssueServiceExtension
import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration

abstract class AbstractTextIssueExportService(
        private val exportFormat: ExportFormat
) : IssueExportService {

    override fun getExportFormat(): ExportFormat = exportFormat

    override fun export(issueServiceExtension: IssueServiceExtension, issueServiceConfiguration: IssueServiceConfiguration, groupedIssues: Map<String, List<Issue>>): ExportedIssues {
        val s = StringBuilder()
        exportAsText(issueServiceExtension, issueServiceConfiguration, groupedIssues, s)
        return ExportedIssues(exportFormat.type, s.toString())
    }

    abstract fun exportAsText(issueServiceExtension: IssueServiceExtension, issueServiceConfiguration: IssueServiceConfiguration, groupedIssues: Map<String, List<Issue>>, s: StringBuilder)
}