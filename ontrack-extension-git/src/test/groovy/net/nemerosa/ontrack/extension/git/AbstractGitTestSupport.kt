package net.nemerosa.ontrack.extension.git

import net.nemerosa.ontrack.extension.git.model.BasicGitConfiguration
import net.nemerosa.ontrack.extension.git.model.ConfiguredBuildGitCommitLink
import net.nemerosa.ontrack.extension.git.property.*
import net.nemerosa.ontrack.extension.git.service.GitConfigurationService
import net.nemerosa.ontrack.extension.git.support.CommitBuildNameGitCommitLink
import net.nemerosa.ontrack.extension.git.support.CommitLinkConfig
import net.nemerosa.ontrack.extension.git.support.GitCommitPropertyCommitLink
import net.nemerosa.ontrack.extension.issues.support.MockIssueServiceConfiguration
import net.nemerosa.ontrack.git.support.GitRepo
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.support.NoConfig
import net.nemerosa.ontrack.test.TestUtils
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractGitTestSupport : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var commitBuildNameGitCommitLink: CommitBuildNameGitCommitLink

    @Autowired
    private lateinit var gitCommitPropertyCommitLink: GitCommitPropertyCommitLink

    @Autowired
    private lateinit var gitConfigurationService: GitConfigurationService

    /**
     * Creates and saves a Git configuration
     */
    protected fun createGitConfiguration(repo: GitRepo): BasicGitConfiguration {
        val gitConfigurationName = TestUtils.uid("C")
        return asUser().with(GlobalSettings::class.java).call {
            gitConfigurationService.newConfiguration(
                    BasicGitConfiguration.empty()
                            .withName(gitConfigurationName)
                            .withIssueServiceConfigurationIdentifier(MockIssueServiceConfiguration.INSTANCE.toIdentifier().format())
                            .withRemote("file://${repo.dir.absolutePath}")
            )
        }
    }

    /**
     * Configures a project for Git.
     */
    protected fun Project.gitProject(repo: GitRepo) {
        // Create a Git configuration
        val gitConfiguration = createGitConfiguration(repo)
        // Configures the project
        setProperty(
                this,
                GitProjectConfigurationPropertyType::class.java,
                GitProjectConfigurationProperty(gitConfiguration)
        )
    }

    /**
     * Configures a branch for Git.
     *
     * @receiver Branch to configure
     * @param branchName Git branch to associate with the branch
     * @param commitLinkConfiguration Returns the build commit link, defaults to [buildNameAsCommit]
     */
    protected fun Branch.gitBranch(
            branchName: String,
            commitLinkConfiguration: () -> ConfiguredBuildGitCommitLink<*> = { buildNameAsCommit() }
    ) {
        asAdmin().execute {
            propertyService.editProperty(
                    this,
                    GitBranchConfigurationPropertyType::class.java,
                    GitBranchConfigurationProperty(
                            branchName,
                            commitLinkConfiguration().toServiceConfiguration(),
                            false, 0
                    )
            )
        }
    }

    /**
     * Configuration of a build commit link based on build name being a commit
     */
    protected fun buildNameAsCommit(abbreviated: Boolean = true): ConfiguredBuildGitCommitLink<CommitLinkConfig> {
        return ConfiguredBuildGitCommitLink(
                commitBuildNameGitCommitLink,
                CommitLinkConfig(abbreviated)
        )
    }

    /**
     * Configuration of a build commit link based on a commit property.
     */
    protected fun commitAsProperty() = ConfiguredBuildGitCommitLink(
            gitCommitPropertyCommitLink,
            NoConfig.INSTANCE
    )

    /**
     * Sets the Git commit property on a build
     */
    protected fun Build.gitCommitProperty(commit: String) {
        setProperty(
                this,
                GitCommitPropertyType::class.java,
                GitCommitProperty(commit)
        )
    }

    /**
     * Creating and initializing a Git repository, running some code in it, and discarding it.
     */
    protected fun withRepo(code: (GitRepo) -> Unit) {
        GitRepo()
                .apply { gitInit() }
                .use(code)
    }

    /**
     * Creates [n] commits, from 1 to [n], with message being "Commit `i`" by default.
     *
     * @param n Number of commits to create
     * @return A map where the key in the index, and the value is the commit hash.
     */
    protected fun GitRepo.commits(n: Int) =
            (1..n).associate {
                it to commit(it, "Commit $it")
            }

}