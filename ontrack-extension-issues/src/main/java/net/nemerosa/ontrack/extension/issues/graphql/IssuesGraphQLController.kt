package net.nemerosa.ontrack.extension.issues.graphql

import net.nemerosa.ontrack.extension.issues.export.ExportFormat
import net.nemerosa.ontrack.extension.issues.export.IssueExportServiceFactory
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class IssuesGraphQLController(
    private val issueExportServiceFactory: IssueExportServiceFactory,
) {

    @QueryMapping
    fun issueExportFormats(): List<ExportFormat> = issueExportServiceFactory.issueExportServices
        .map { it.exportFormat }
        .sortedBy { it.name }

}