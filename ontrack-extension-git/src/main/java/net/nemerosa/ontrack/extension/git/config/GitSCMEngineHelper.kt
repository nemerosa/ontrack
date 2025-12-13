package net.nemerosa.ontrack.extension.git.config

import net.nemerosa.ontrack.extension.git.model.ConfiguredBuildGitCommitLink
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.git.property.GitCommitProperty
import net.nemerosa.ontrack.extension.git.property.GitCommitPropertyType
import net.nemerosa.ontrack.extension.git.support.GitCommitPropertyCommitLink
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.support.NoConfig
import org.springframework.stereotype.Component

@Component
class GitSCMEngineHelper(
    private val propertyService: PropertyService,
    private val gitCommitPropertyCommitLink: GitCommitPropertyCommitLink,
) {

    fun configureBranch(
        branch: Branch,
        scmBranch: String
    ) {
        propertyService.editProperty(
            entity = branch,
            propertyType = GitBranchConfigurationPropertyType::class.java,
            data = GitBranchConfigurationProperty(
                branch = scmBranch,
                buildCommitLink = ConfiguredBuildGitCommitLink(
                    link = gitCommitPropertyCommitLink,
                    data = NoConfig.INSTANCE,
                ).toServiceConfiguration(),
                override = false,
                buildTagInterval = 0,
            )
        )
    }

    fun configureBuild(build: Build, commit: String) {
        propertyService.editProperty(
            entity = build,
            propertyType = GitCommitPropertyType::class.java,
            data = GitCommitProperty(commit = commit),
        )
    }


}