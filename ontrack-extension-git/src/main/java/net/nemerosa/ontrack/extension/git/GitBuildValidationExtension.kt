package net.nemerosa.ontrack.extension.git

import net.nemerosa.ontrack.extension.api.BuildValidationExtension
import net.nemerosa.ontrack.extension.api.model.BuildValidationException
import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.Build
import org.springframework.stereotype.Component

@Component
class GitBuildValidationExtension(
        extensionFeature: GitExtensionFeature,
        private val gitService: GitService
) : AbstractExtension(extensionFeature), BuildValidationExtension {

    override fun validateBuild(build: Build) {
        // Gets the Git branch configuration
        val branchConfiguration = gitService.getBranchConfiguration(build.branch)
        if (branchConfiguration != null) {
            if (branchConfiguration.buildCommitLink != null && !branchConfiguration.buildCommitLink.isBuildNameValid(build.name)) {
                throw BuildValidationException(String.format(
                        "Build name %s is not valid for the branch Git configuration",
                        build.name
                ))
            }
        }
    }
}
