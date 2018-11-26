package net.nemerosa.ontrack.extension.git

import net.nemerosa.ontrack.extension.git.model.ConfiguredBuildGitCommitLink
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.git.support.CommitBuildNameGitCommitLink
import net.nemerosa.ontrack.extension.git.support.CommitLinkConfig
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.structure.Branch

abstract class AbstractGitTestSupport : AbstractDSLTestSupport() {

    /**
     * Configures a branch for Git.
     *
     * @receiver Branch to configure
     * @param branchName Git branch to associate with the branch
     * @param commitLinkConfiguration Returns the build commit link, defaults to [commitProperty]
     */
    protected fun Branch.gitBranch(
            branchName: String,
            commitLinkConfiguration: () -> ConfiguredBuildGitCommitLink<*> = { commitProperty() }
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
     * Configuration of a build commit link based on commit property
     */
    protected fun commitProperty(abbreviated: Boolean = true): ConfiguredBuildGitCommitLink<CommitLinkConfig> {
        return ConfiguredBuildGitCommitLink(
                CommitBuildNameGitCommitLink(),
                CommitLinkConfig(abbreviated)
        )
    }


}