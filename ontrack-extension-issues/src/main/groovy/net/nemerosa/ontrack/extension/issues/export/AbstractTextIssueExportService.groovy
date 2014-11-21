package net.nemerosa.ontrack.extension.issues.export

import net.nemerosa.ontrack.extension.issues.IssueServiceExtension
import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration

abstract class AbstractTextIssueExportService implements IssueExportService {

    private final ExportFormat exportFormat

    AbstractTextIssueExportService(ExportFormat exportFormat) {
        this.exportFormat = exportFormat
    }

    @Override
    ExportFormat getExportFormat() {
        exportFormat
    }

    @Override
    ExportedIssues export(IssueServiceExtension issueServiceExtension, IssueServiceConfiguration issueServiceConfiguration, Map<String, List<Issue>> groupedIssues) {
        StringBuilder s = new StringBuilder()
        exportAsText(issueServiceExtension, issueServiceConfiguration, groupedIssues, s)
        new ExportedIssues(
                exportFormat.type,
                s.toString()
        )
    }

    abstract void exportAsText(IssueServiceExtension issueServiceExtension, IssueServiceConfiguration issueServiceConfiguration, Map<String, List<Issue>> groupedIssues, StringBuilder s)
}