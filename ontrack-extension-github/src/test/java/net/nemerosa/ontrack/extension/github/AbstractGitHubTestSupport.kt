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

    protected fun gitHubConfiguration(
        gitConfigurationName: String = uid("C"),
        url: String? = null,
        username: String? = null,
        password: String? = null,
        token: String? = null,
        appId: String? = null,
        appPrivateKey: String? = null,
        appInstallationAccountName: String? = null,
    ): GitHubEngineConfiguration {
        return withDisabledConfigurationTest {
            asUser().with(GlobalSettings::class.java).call {
                gitConfigurationService.newConfiguration(
                    GitHubEngineConfiguration(
                        name = gitConfigurationName,
                        url = url,
                        user = username,
                        password = password,
                        oauth2Token = token,
                        appId = appId,
                        appPrivateKey = appPrivateKey,
                        appInstallationAccountName = appInstallationAccountName,
                    )
                )
            }
        }
    }

    protected fun Project.configureGitHub(
        gitHubConfiguration: GitHubEngineConfiguration = gitHubConfiguration(),
        issueServiceConfigurationIdentifier: String? = null,
    ): GitHubProjectConfigurationProperty {
        // Create a Git configuration
        val actualIssueServiceConfigurationIdentifier: String? = when (issueServiceConfigurationIdentifier) {
            null -> null
            "github" -> "github//${gitHubConfiguration.name}:nemerosa/test"
            else -> issueServiceConfigurationIdentifier
        }
        val property = GitHubProjectConfigurationProperty(
            gitHubConfiguration,
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

    protected fun Branch.gitRealConfig(
        branch: String = githubTestEnv.branch,
    ) {
        setProperty(
            this,
            GitBranchConfigurationPropertyType::class.java,
            GitBranchConfigurationProperty(
                branch = branch,
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