package net.nemerosa.ontrack.extension.github.property

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.github.AbstractGitHubTestSupport
import net.nemerosa.ontrack.extension.github.GitHubIssueServiceExtension
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.test.assertIs
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertNotNull

class GitHubProjectConfigurationPropertyTypeIT : AbstractGitHubTestSupport() {

    @Autowired
    private lateinit var gitHubConfigurator: GitHubConfigurator

    @Test
    fun `Setting GitHub issues as issue service`() {
        asAdmin {
            val cfg = gitHubConfig()
            project {
                propertyService.editProperty(this, GitHubProjectConfigurationPropertyType::class.java.name, mapOf(
                    "configuration" to cfg.name,
                    "repository" to "nemerosa/ontrack",
                    "indexationInterval" to 0,
                    "issueServiceConfigurationIdentifier" to "self"
                ).asJson())
                // Gets the issue service configuration
                assertNotNull(gitHubConfigurator.getConfiguration(this)) { gitConfiguration ->
                    assertNotNull(gitConfiguration.configuredIssueService.getOrNull()) { configuredIssueService ->
                        assertIs<GitHubIssueServiceExtension>(configuredIssueService.issueServiceExtension)
                    }
                }
            }
        }
    }

}