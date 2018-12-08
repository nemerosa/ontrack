package net.nemerosa.ontrack.extension.git.model

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertNull

class BasicGitConfigurationIssueServiceProviderIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var basicGitConfigurationIssueServiceProvider: BasicGitConfigurationIssueServiceProvider

    @Test
    fun `No Git configuration`() {
        project {
            val configuredIssueService = basicGitConfigurationIssueServiceProvider.getConfiguredIssueService(this)
            assertNull(configuredIssueService, "No configured issue service when no Git configuration")
        }
    }

}