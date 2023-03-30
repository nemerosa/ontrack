package net.nemerosa.ontrack.extension.issues.mock

import io.mockk.mockk
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration

class TestIssueServiceConfiguration(
    override val name: String,
) : IssueServiceConfiguration {

    companion object {
        val INSTANCE = TestIssueServiceConfiguration("default")

        fun configuredIssueService(name: String): ConfiguredIssueService {
            return ConfiguredIssueService(
                TestIssueServiceExtension(
                    TestIssueServiceFeature(),
                    mockk()
                ),
                TestIssueServiceConfiguration(name)
            )
        }
    }

    override val serviceId: String = "test"
}
