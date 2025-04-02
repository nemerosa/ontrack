package net.nemerosa.ontrack.extension.issues.export

@Deprecated("Will be removed in V5. Use the templating service.")
interface IssueExportServiceFactory {

    fun getIssueExportService(format: String): IssueExportService?

    val issueExportServices: Collection<IssueExportService>

}