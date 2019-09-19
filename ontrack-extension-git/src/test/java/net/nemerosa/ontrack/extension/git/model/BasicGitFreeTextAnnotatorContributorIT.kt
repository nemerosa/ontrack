package net.nemerosa.ontrack.extension.git.model

import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.git.service.GitConfigurationService
import net.nemerosa.ontrack.extension.issues.support.MockIssueServiceConfiguration
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.support.MessageAnnotationUtils
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class BasicGitFreeTextAnnotatorContributorIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var gitConfigurationService: GitConfigurationService

    @Autowired
    private lateinit var gitFreeTextAnnotatorContributor: GitFreeTextAnnotatorContributor

    @Test
    fun `No Git configuration`() {
        project {
            expects("Text with #123" to "Text with #123")
        }
    }

    @Test
    fun `Git configuration without any issue service`() {
        project {
            basicGitConfig(issueServiceConfigurationIdentifier = null)
            expects("Text with #123" to """Text with #123""")
        }
    }

    @Test
    fun `Git configuration with an issue service`() {
        project {
            basicGitConfig(issueServiceConfigurationIdentifier = MockIssueServiceConfiguration.INSTANCE.toIdentifier().format())
            expects("Text with #123" to """Text with <a href="http://issue/123">#123</a>""")
        }
    }

    private fun Project.basicGitConfig(issueServiceConfigurationIdentifier: String?) {
        // Create a Git configuration
        val gitConfigurationName = uid("C")
        val gitConfiguration = withDisabledConfigurationTest {
            asUser().with(GlobalSettings::class.java).call {
                gitConfigurationService.newConfiguration(
                        BasicGitConfiguration.empty()
                                .withName(gitConfigurationName)
                                .withIssueServiceConfigurationIdentifier(issueServiceConfigurationIdentifier)
                )
            }
        }
        setProperty(
                this,
                GitProjectConfigurationPropertyType::class.java,
                GitProjectConfigurationProperty(gitConfiguration)
        )
    }

    private fun Project.expects(transformation: Pair<String, String>) {
        val input = transformation.first
        val expected = transformation.second
        // Gets the annotators
        val annotators = gitFreeTextAnnotatorContributor.getMessageAnnotators(this)
        // Annotation
        val actual = MessageAnnotationUtils.annotate(input, annotators)
        // Comparison
        assertEquals(expected, actual)
    }

}