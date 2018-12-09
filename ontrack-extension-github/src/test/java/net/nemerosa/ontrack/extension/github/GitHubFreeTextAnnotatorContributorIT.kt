package net.nemerosa.ontrack.extension.github

import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationProperty
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.github.service.GitHubConfigurationService
import net.nemerosa.ontrack.extension.issues.support.MockIssueServiceConfiguration
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.support.MessageAnnotationUtils
import net.nemerosa.ontrack.test.TestUtils
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class GitHubFreeTextAnnotatorContributorIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var gitConfigurationService: GitHubConfigurationService

    @Autowired
    private lateinit var gitHubFreeTextAnnotatorContributor: GitHubFreeTextAnnotatorContributor

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
            expects("Text with #123" to """Text with #123""")
        }
    }

    @Test
    fun `GitHub configuration with own issue service`() {
        project {
            gitHubConfig(issueServiceConfigurationIdentifier = "github")
            expects("Text with #123" to """Text with <a href="https://github.com/nemerosa/test/issues/123">#123</a>""")
        }
    }

    @Test
    fun `GitHub configuration with an issue service`() {
        project {
            gitHubConfig(issueServiceConfigurationIdentifier = MockIssueServiceConfiguration.INSTANCE.toIdentifier().format())
            expects("Text with #123" to """Text with <a href="http://issue/123">#123</a>""")
        }
    }

    private fun Project.gitHubConfig(issueServiceConfigurationIdentifier: String?) {
        // Create a Git configuration
        val gitConfigurationName = TestUtils.uid("C")
        val gitConfiguration = withDisabledConfigurationTest {
            asUser().with(GlobalSettings::class.java).call {
                gitConfigurationService.newConfiguration(
                        GitHubEngineConfiguration(
                                gitConfigurationName,
                                null,
                                null,
                                null,
                                null
                        )
                )
            }
        }
        val actualIssueServiceConfigurationIdentifier: String? = when (issueServiceConfigurationIdentifier) {
            null -> null
            "github" -> "github//${gitConfigurationName}:nemerosa/test"
            else -> issueServiceConfigurationIdentifier
        }
        setProperty(
                this,
                GitHubProjectConfigurationPropertyType::class.java,
                GitHubProjectConfigurationProperty(
                        gitConfiguration,
                        "nemerosa/test",
                        0,
                        actualIssueServiceConfigurationIdentifier
                )
        )
    }

    private fun Project.expects(transformation: Pair<String, String>) {
        val input = transformation.first
        val expected = transformation.second
        // Gets the annotators
        val annotators = gitHubFreeTextAnnotatorContributor.getMessageAnnotators(this)
        // Annotation
        val actual = MessageAnnotationUtils.annotate(input, annotators)
        // Comparison
        assertEquals(expected, actual)
    }
}