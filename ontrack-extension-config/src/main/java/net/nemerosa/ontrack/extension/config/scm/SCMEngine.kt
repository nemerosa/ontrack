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
     *
     * @param project Project to configure
     * @param configuration Configuration for the project
     * @param env Environment variables to use for the configuration
     * @param projectName Name of the project as defined by the CI engine.
     */
    fun configureProject(
        project: Project,
        configuration: ProjectConfiguration,
        env: Map<String, String>,
        projectName: String
    )

    /**
     * Configures a branch with this SCM engine.
     *
     * @param branch Branch to configure
     * @param configuration Configuration for the branch
     * @param env Environment variables to use for the configuration
     * @param scmBranch Name of the SCM branch as defined by the CI engine.
     */
    fun configureBranch(branch: Branch, configuration: BranchConfiguration, env: Map<String, String>, scmBranch: String)

    /**
     * Configures a build with this SCM engine.
     *
     * @param build Build to configure
     * @param configuration Configuration for the build
     * @param env Environment variables to use for the configuration
     */
    fun configureBuild(build: Build, configuration: BuildConfiguration, env: Map<String, String>)

    /**
     * Checks if the given SCM URL matches this SCM engine.
     *
     * @param scmUrl URL to check
     * @return `true` if the URL matches this SCM engine
     */
    fun matchesUrl(scmUrl: String): Boolean

    /**
     * Identifier for the SCM engine.
     */
    val name: String
}