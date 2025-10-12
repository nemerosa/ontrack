package net.nemerosa.ontrack.extension.config.scm

import net.nemerosa.ontrack.extension.config.model.BranchConfiguration
import net.nemerosa.ontrack.extension.config.model.BuildConfiguration
import net.nemerosa.ontrack.extension.config.model.EnvConstants
import net.nemerosa.ontrack.extension.config.model.ProjectConfiguration
import net.nemerosa.ontrack.extension.scm.mock.*
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

@Component
class MockSCMEngine(
    propertyService: PropertyService,
) : AbstractSCMEngine(
    propertyService = propertyService,
    name = "mock"
) {

    override fun matchesUrl(scmUrl: String): Boolean = false

    override fun configureProject(
        project: Project,
        configuration: ProjectConfiguration,
        env: Map<String, String>,
        projectName: String,
    ) {
        propertyService.editProperty(
            entity = project,
            propertyType = MockSCMProjectPropertyType::class.java,
            data = MockSCMProjectProperty(
                name = projectName,
                issueServiceIdentifier = configuration.issueServiceIdentifier?.toRepresentation(),
            ),
        )
    }

    override fun configureBranch(
        branch: Branch,
        configuration: BranchConfiguration,
        env: Map<String, String>,
        scmBranch: String,
    ) {
        propertyService.editProperty(
            entity = branch,
            propertyType = MockSCMBranchPropertyType::class.java,
            data = MockSCMBranchProperty(scmBranch),
        )
    }

    override fun configureBuild(
        build: Build,
        configuration: BuildConfiguration,
        env: Map<String, String>
    ) {
        val id = env[EnvConstants.GENERIC_BUILD_REVISION]
        if (!id.isNullOrBlank()) {
            propertyService.editProperty(
                entity = build,
                propertyType = MockSCMBuildCommitPropertyType::class.java,
                data = MockSCMBuildCommitProperty(id),
            )
        }
    }
}