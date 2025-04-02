package net.nemerosa.ontrack.extension.issues.mock

import net.nemerosa.ontrack.extension.issues.export.IssueExportService
import net.nemerosa.ontrack.extension.issues.export.IssueExportServiceFactory
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration

class TestIssueServiceConfiguration(
    override val name: String,
) : IssueServiceConfiguration {

    companion object {
        val INSTANCE = TestIssueServiceConfiguration("default")

        @Deprecated("Will be removed in V5. Use the templating service.")
        fun configuredIssueService(name: String): ConfiguredIssueService {
            return ConfiguredIssueService(
                TestIssueServiceExtension(
                    extensionFeature = TestIssueServiceFeature(),
                    issueExportServiceFactory = object : IssueExportServiceFactory {
                        override fun getIssueExportService(format: String): IssueExportService? {
                            TODO("Not yet implemented")
                        }

                        override val issueExportServices: Collection<IssueExportService>
                            get() = TODO("Not yet implemented")
                    }
                ),
                TestIssueServiceConfiguration(name)
            )
        }
    }

    override val serviceId: String = "test"
}
