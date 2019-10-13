package net.nemerosa.ontrack.extension.issues.export

import org.springframework.stereotype.Service

@Service
internal class IssueExportServiceFactoryImpl(issueExportServices: Collection<IssueExportService>) : IssueExportServiceFactory {

    private val issueExportServiceMap: Map<String, IssueExportService> = issueExportServices.associateBy {
        it.exportFormat.id
    }

    @Throws(IssueExportServiceNotFoundException::class)
    override fun getIssueExportService(format: String): IssueExportService {
        val issueExportService = issueExportServiceMap[format]
        return issueExportService ?: throw IssueExportServiceNotFoundException(format)
    }

    override fun getIssueExportServices(): Collection<IssueExportService> {
        return issueExportServiceMap.values
    }
}
