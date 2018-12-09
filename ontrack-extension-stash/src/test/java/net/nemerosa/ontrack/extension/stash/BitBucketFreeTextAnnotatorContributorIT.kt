package net.nemerosa.ontrack.extension.stash

import net.nemerosa.ontrack.extension.git.model.GitFreeTextAnnotatorContributor
import net.nemerosa.ontrack.extension.issues.support.MockIssueServiceConfiguration
import net.nemerosa.ontrack.extension.stash.model.StashConfiguration
import net.nemerosa.ontrack.extension.stash.property.StashProjectConfigurationProperty
import net.nemerosa.ontrack.extension.stash.property.StashProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.stash.service.StashConfigurationService
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.support.MessageAnnotationUtils
import net.nemerosa.ontrack.test.TestUtils
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class BitBucketFreeTextAnnotatorContributorIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var stashConfigurationService: StashConfigurationService

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
            bitBucketConfig(issueServiceConfigurationIdentifier = null)
            expects("Text with #123" to """Text with #123""")
        }
    }

    @Test
    fun `Git configuration with an issue service`() {
        project {
            bitBucketConfig(issueServiceConfigurationIdentifier = MockIssueServiceConfiguration.INSTANCE.toIdentifier().format())
            expects("Text with #123" to """Text with <a href="http://issue/123">#123</a>""")
        }
    }

    private fun Project.bitBucketConfig(issueServiceConfigurationIdentifier: String?) {
        // Create a Git configuration
        val gitConfigurationName = TestUtils.uid("C")
        val gitConfiguration = withDisabledConfigurationTest {
            asUser().with(GlobalSettings::class.java).call {
                stashConfigurationService.newConfiguration(
                        StashConfiguration(
                                gitConfigurationName,
                                "https://bitbucket.nemerosa.net",
                                "test",
                                "xxx"
                        )
                )
            }
        }
        setProperty(
                this,
                StashProjectConfigurationPropertyType::class.java,
                StashProjectConfigurationProperty(
                        gitConfiguration,
                        "nemerosa",
                        "test",
                        0,
                        issueServiceConfigurationIdentifier
                )
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
        kotlin.test.assertEquals(expected, actual)
    }
}