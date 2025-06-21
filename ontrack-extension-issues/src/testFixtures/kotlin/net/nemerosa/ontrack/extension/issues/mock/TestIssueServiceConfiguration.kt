package net.nemerosa.ontrack.extension.issues.mock

import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration

class TestIssueServiceConfiguration(
    override val name: String,
) : IssueServiceConfiguration {

    companion object {
        val INSTANCE = TestIssueServiceConfiguration("default")
    }

    override val serviceId: String = "test"
}
