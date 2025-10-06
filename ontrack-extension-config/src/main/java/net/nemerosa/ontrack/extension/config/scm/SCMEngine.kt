package net.nemerosa.ontrack.extension.config.scm

import net.nemerosa.ontrack.extension.config.model.BranchConfiguration
import net.nemerosa.ontrack.extension.config.model.BuildConfiguration
import net.nemerosa.ontrack.extension.config.model.ProjectConfiguration
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.Project

interface SCMEngine {
    fun normalizeBranchName(rawBranchName: String): String = NameDescription.escapeName(rawBranchName)

    /**
     * Configures a project with this SCM engine.
     */
    fun configureProject(project: Project, configuration: ProjectConfiguration, env: Map<String, String>, projectName: String)

    /**
     * Configures a branch with this SCM engine.
     */
    fun configureBranch(branch: Branch, configuration: BranchConfiguration, env: Map<String, String>, scmBranch: String)

    /**
     * Configures a build with this SCM engine.
     */
    fun configureBuild(build: Build, configuration: BuildConfiguration, env: Map<String, String>)

    val name: String
}