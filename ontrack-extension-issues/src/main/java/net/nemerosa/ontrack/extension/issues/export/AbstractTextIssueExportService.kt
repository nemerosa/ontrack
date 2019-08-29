package net.nemerosa.ontrack.extension.issues.export

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.extension.issues.IssueServiceExtension
import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration

abstract class AbstractTextIssueExportService(
        override val exportFormat: ExportFormat
) : IssueExportService {

    override fun export(issueServiceExtension: IssueServiceExtension, issueServiceConfiguration: IssueServiceConfiguration, groupedIssues: Map<String, List<Issue>>): ExportedIssues {
        val s = StringBuilder()
        exportAsText(issueServiceExtension, issueServiceConfiguration, groupedIssues, s)
        return ExportedIssues(exportFormat.type, s.toString())
    }

    override fun exportSection(title: String?, sectionType: SectionType, content: Document): Document {
        return if (content.type != exportFormat.type) {
            throw ExportFormatIncompatibleException()
        } else {
            Document(
                    exportFormat.type,
                    exportSectionAsText(title, sectionType, content.content.toString(Charsets.UTF_8)).toByteArray()
            )
        }
    }

    override fun concatSections(sections: Collection<Document>): Document {
        return if (sections.any { it.type != exportFormat.type }) {
            throw ExportFormatIncompatibleException()
        } else {
            Document(
                    exportFormat.type,
                    concatText(sections.map { it.content.toString(Charsets.UTF_8) }).toByteArray()
            )
        }
    }

    abstract fun exportAsText(issueServiceExtension: IssueServiceExtension, issueServiceConfiguration: IssueServiceConfiguration, groupedIssues: Map<String, List<Issue>>, s: StringBuilder)

    abstract fun exportSectionAsText(title: String?, sectionType: SectionType, content: String): String


    open fun concatText(sections: Collection<String>): String = sections.joinToString("")
}