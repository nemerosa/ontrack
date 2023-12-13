package net.nemerosa.ontrack.extension.git

import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

@Component
class GitTestSupport(
    private val propertyService: PropertyService,
) {

    fun setGitBranchConfigurationProperty(branch: Branch, scmBranch: String) {
        propertyService.editProperty(
            branch,
            GitBranchConfigurationPropertyType::class.java,
            GitBranchConfigurationProperty(
                branch = scmBranch,
                buildCommitLink = null,
                isOverride = false,
                buildTagInterval = 0,
            )
        )
    }

}