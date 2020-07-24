package net.nemerosa.ontrack.extension.git.model

import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationProperty
import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.model.structure.Branch

fun GitService.getBranchAsPullRequest(
        branch: Branch,
        gitBranchConfigurationProperty: GitBranchConfigurationProperty?
) = gitBranchConfigurationProperty?.let {
    getGitConfiguratorAndConfiguration(branch.project)
            ?.let { (configurator, configuration) ->
                configurator.toPullRequestID(gitBranchConfigurationProperty.branch)?.let { prId ->
                    configurator.getPullRequest(configuration, prId)
                            ?: GitPullRequest.invalidPR(prId, configurator.toPullRequestKey(prId))
                }
            }
}