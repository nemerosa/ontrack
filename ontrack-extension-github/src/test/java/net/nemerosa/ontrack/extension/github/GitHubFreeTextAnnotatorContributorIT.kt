package net.nemerosa.ontrack.extension.github

import net.nemerosa.ontrack.extension.issues.mock.TestIssueServiceConfiguration
import net.nemerosa.ontrack.extension.issues.model.toIdentifier
import org.junit.jupiter.api.Test

class GitHubFreeTextAnnotatorContributorIT : AbstractGitHubTestSupport() {

    @Test
    fun `GitHub Git configuration`() {
        project {
            expects("Text with #123" to "Text with #123")
        }
    }

    @Test
    fun `GitHub configuration without any issue service`() {
        project {
            configureGitHub(issueServiceConfigurationIdentifier = null)
            expects("Text with #123" to """Text with <a href="https://github.com/nemerosa/test/issues/123">#123</a>""")
        }
    }

    @Test
    fun `GitHub configuration with full issue service`() {
        project {
            configureGitHub(issueServiceConfigurationIdentifier = "self")
            expects("Text with #123" to """Text with <a href="https://github.com/nemerosa/test/issues/123">#123</a>""")
        }
    }

    @Test
    fun `GitHub configuration with own full issue service`() {
        project {
            configureGitHub(issueServiceConfigurationIdentifier = "github")
            expects("Text with #123" to """Text with <a href="https://github.com/nemerosa/test/issues/123">#123</a>""")
        }
    }

    @Test
    fun `GitHub configuration with an issue service`() {
        project {
            configureGitHub(issueServiceConfigurationIdentifier = TestIssueServiceConfiguration.INSTANCE.toIdentifier()
                .format())
            expects("Text with #123" to """Text with <a href="http://issue/123">#123</a>""")
        }
    }
}