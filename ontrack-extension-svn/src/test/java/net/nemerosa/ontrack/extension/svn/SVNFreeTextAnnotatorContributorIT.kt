package net.nemerosa.ontrack.extension.svn

import net.nemerosa.ontrack.extension.issues.support.MockIssueServiceConfiguration
import net.nemerosa.ontrack.extension.svn.model.SVNConfiguration
import net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationProperty
import net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.svn.service.SVNConfigurationService
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.support.MessageAnnotationUtils
import net.nemerosa.ontrack.test.TestUtils
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class SVNFreeTextAnnotatorContributorIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var svnConfigurationService: SVNConfigurationService

    @Autowired
    private lateinit var svnFreeTextAnnotatorContributor: SVNFreeTextAnnotatorContributor

    @Test
    fun `No SVN configuration`() {
        project {
            expects("Text with #123" to "Text with #123")
        }
    }

    @Test
    fun `SVN configuration without any issue service`() {
        project {
            svnConfig(issueServiceConfigurationIdentifier = null)
            expects("Text with #123" to """Text with #123""")
        }
    }

    @Test
    fun `SVN configuration with an issue service`() {
        project {
            svnConfig(issueServiceConfigurationIdentifier = MockIssueServiceConfiguration.INSTANCE.toIdentifier().format())
            expects("Text with #123" to """Text with <a href="http://issue/123">#123</a>""")
        }
    }

    private fun Project.svnConfig(issueServiceConfigurationIdentifier: String?) {
        // Create a SVN configuration
        val svnConfigurationName = TestUtils.uid("C")
        val svnConfiguration = withDisabledConfigurationTest {
            asUser().with(GlobalSettings::class.java).call {
                svnConfigurationService.newConfiguration(
                        SVNConfiguration.of(
                                svnConfigurationName,
                                "https://bitbucket.nemerosa.net"
                        ).withIssueServiceConfigurationIdentifier(issueServiceConfigurationIdentifier)
                )
            }
        }
        setProperty(
                this,
                SVNProjectConfigurationPropertyType::class.java,
                SVNProjectConfigurationProperty(
                        svnConfiguration,
                        "trunk"
                )
        )
    }

    private fun Project.expects(transformation: Pair<String, String>) {
        val input = transformation.first
        val expected = transformation.second
        // Gets the annotators
        val annotators = svnFreeTextAnnotatorContributor.getMessageAnnotators(this)
        // Annotation
        val actual = MessageAnnotationUtils.annotate(input, annotators)
        // Comparison
        kotlin.test.assertEquals(expected, actual)
    }
}