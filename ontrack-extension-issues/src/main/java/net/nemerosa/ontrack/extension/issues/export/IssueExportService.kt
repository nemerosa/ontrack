package net.nemerosa.ontrack.extension.issues.export

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.extension.issues.IssueServiceExtension
import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration

interface IssueExportService {

    val exportFormat: ExportFormat

    fun export(
            issueServiceExtension: IssueServiceExtension,
            issueServiceConfiguration: IssueServiceConfiguration,
            groupedIssues: Map<String, List<Issue>>): ExportedIssues

    fun exportSection(
            title: String?,
            sectionType: SectionType,
            content: Document
    ): Document

    fun concatSections(sections: Collection<Document>): Document

    companion object {
        const val NO_GROUP = ""
    }
}