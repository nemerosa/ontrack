package net.nemerosa.ontrack.extension.github

import net.nemerosa.ontrack.extension.issues.support.MockIssueServiceConfiguration
import org.junit.Test

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
            gitHubConfig(issueServiceConfigurationIdentifier = null)
            expects("Text with #123" to """Text with <a href="https://github.com/nemerosa/test/issues/123">#123</a>""")
        }
    }

    @Test
    fun `GitHub configuration with full issue service`() {
        project {
            gitHubConfig(issueServiceConfigurationIdentifier = "self")
            expects("Text with #123" to """Text with <a href="https://github.com/nemerosa/test/issues/123">#123</a>""")
        }
    }

    @Test
    fun `GitHub configuration with own full issue service`() {
        project {
            gitHubConfig(issueServiceConfigurationIdentifier = "github")
            expects("Text with #123" to """Text with <a href="https://github.com/nemerosa/test/issues/123">#123</a>""")
        }
    }

    @Test
    fun `GitHub configuration with an issue service`() {
        project {
            gitHubConfig(issueServiceConfigurationIdentifier = MockIssueServiceConfiguration.INSTANCE.toIdentifier()
                .format())
            expects("Text with #123" to """Text with <a href="http://issue/123">#123</a>""")
        }
    }
}