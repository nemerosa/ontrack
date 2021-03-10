package net.nemerosa.ontrack.extension.github

import net.nemerosa.ontrack.extension.git.model.GitFreeTextAnnotatorContributor
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationProperty
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.github.service.GitHubConfigurationService
import net.nemerosa.ontrack.extension.issues.support.MockIssueServiceConfiguration
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.support.MessageAnnotationUtils
import net.nemerosa.ontrack.test.TestUtils
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

abstract class AbstractGitHubTestSupport : AbstractQLKTITSupport() {

    @Autowired
    protected lateinit var gitConfigurationService: GitHubConfigurationService

    @Autowired
    protected lateinit var gitFreeTextAnnotatorContributor: GitFreeTextAnnotatorContributor

    protected fun gitHubConfig(gitConfigurationName: String = uid("C")): GitHubEngineConfiguration {
        return withDisabledConfigurationTest {
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
    }

    protected fun Project.gitHubConfig(issueServiceConfigurationIdentifier: String?): GitHubProjectConfigurationProperty {
        // Create a Git configuration
        val gitConfiguration = gitHubConfig()
        val actualIssueServiceConfigurationIdentifier: String? = when (issueServiceConfigurationIdentifier) {
            null -> null
            "github" -> "github//${gitConfiguration.name}:nemerosa/test"
            else -> issueServiceConfigurationIdentifier
        }
        val property = GitHubProjectConfigurationProperty(
            gitConfiguration,
            "nemerosa/test",
            0,
            actualIssueServiceConfigurationIdentifier
        )
        setProperty(
            this,
            GitHubProjectConfigurationPropertyType::class.java,
            property
        )
        return property
    }

    protected fun Project.expects(transformation: Pair<String, String>) {
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