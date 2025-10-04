package net.nemerosa.ontrack.extension.config.model

import net.nemerosa.ontrack.extension.config.ci.engine.CIEngine
import net.nemerosa.ontrack.extension.config.scm.SCMEngine
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.Project

interface CoreConfigurationService {
    fun configureProject(
        input: ConfigurationInput,
        configuration: ProjectConfiguration,
        ciEngine: CIEngine,
        scmEngine: SCMEngine,
        env: Map<String, String>
    ): Project

    fun configureBranch(
        project: Project,
        input: ConfigurationInput,
        configuration: BranchConfiguration,
        ciEngine: CIEngine,
        scmEngine: SCMEngine,
        env: Map<String, String>
    ): Branch

    fun configureBuild(
        branch: Branch,
        input: ConfigurationInput,
        configuration: BuildConfiguration,
        ciEngine: CIEngine,
        scmEngine: SCMEngine,
        env: Map<String, String>
    ): Build
}