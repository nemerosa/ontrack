package net.nemerosa.ontrack.extension.github.config

import net.nemerosa.ontrack.extension.config.model.BranchConfiguration
import net.nemerosa.ontrack.extension.config.model.BuildConfiguration
import net.nemerosa.ontrack.extension.config.model.ProjectConfiguration
import net.nemerosa.ontrack.extension.config.scm.AbstractSCMEngine
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

@Component
class GitHubSCMEngine(
    propertyService: PropertyService,
) : AbstractSCMEngine(
    propertyService = propertyService,
    name = "github",
) {
    override fun configureProject(
        project: Project,
        configuration: ProjectConfiguration,
        env: Map<String, String>,
        projectName: String
    ) {
        TODO("Not yet implemented")
    }

    override fun configureBranch(
        branch: Branch,
        configuration: BranchConfiguration,
        env: Map<String, String>,
        scmBranch: String
    ) {
        TODO("Not yet implemented")
    }

    override fun configureBuild(
        build: Build,
        configuration: BuildConfiguration,
        env: Map<String, String>
    ) {
        TODO("Not yet implemented")
    }

    override fun matchesUrl(scmUrl: String): Boolean =
        scmUrl.startsWith("https://github.com/") ||
                scmUrl.startsWith("git@github.com:")
}