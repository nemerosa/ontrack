package net.nemerosa.ontrack.extension.github

import net.nemerosa.ontrack.extension.git.model.ConfiguredBuildGitCommitLink
import net.nemerosa.ontrack.extension.git.model.GitFreeTextAnnotatorContributor
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.git.support.GitCommitPropertyCommitLink
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationProperty
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.github.service.GitHubConfigurationService
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.support.MessageAnnotationUtils
import net.nemerosa.ontrack.model.support.NoConfig
import net.nemerosa.ontrack.test.TestUtils.uid
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

abstract class AbstractGitHubTestSupport : AbstractQLKTITSupport() {

    @Autowired
    protected lateinit var gitConfigurationService: GitHubConfigurationService

    @Autowired
    protected lateinit var gitFreeTextAnnotatorContributor: GitFreeTextAnnotatorContributor

    @Autowired
    private lateinit var gitCommitPropertyCommitLink: GitCommitPropertyCommitLink

    protected fun gitHubConfig(
        gitConfigurationName: String = uid("C"),
        url: String? = null
    ): GitHubEngineConfiguration {
        return withDisabledConfigurationTest {
            asUser().with(GlobalSettings::class.java).call {
                gitConfigurationService.newConfiguration(
                    GitHubEngineConfiguration(
                        gitConfigurationName,
                        url,
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

    protected fun Branch.gitRealConfig() {
        setProperty(
            this,
            GitBranchConfigurationPropertyType::class.java,
            GitBranchConfigurationProperty(
                branch = githubTestEnv.branch,
                buildCommitLink = ConfiguredBuildGitCommitLink(
                    gitCommitPropertyCommitLink,
                    NoConfig.INSTANCE
                ).toServiceConfiguration(),
                isOverride = false,
                buildTagInterval = 0,
            )
        )
    }

    protected fun Project.gitHubRealConfig() {
        // Create a Git configuration
        val gitConfiguration = githubTestConfigReal()
        asUser().with(GlobalSettings::class.java).call {
            gitConfigurationService.newConfiguration(gitConfiguration)
        }
        // Project property
        setProperty(
            this,
            GitHubProjectConfigurationPropertyType::class.java,
            GitHubProjectConfigurationProperty(
                gitConfiguration,
                githubTestEnv.fullRepository,
                0,
                null
            )
        )
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