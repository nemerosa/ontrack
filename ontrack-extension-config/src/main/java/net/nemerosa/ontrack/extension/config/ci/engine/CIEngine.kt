package net.nemerosa.ontrack.extension.config.ci.engine

import net.nemerosa.ontrack.extension.config.model.BuildConfiguration
import net.nemerosa.ontrack.extension.config.model.EnvConstants
import net.nemerosa.ontrack.model.structure.Build

interface CIEngine {

    /**
     * Given a CI environment, returns the project name.
     */
    fun getProjectName(env: Map<String, String>): String? = env[EnvConstants.GENERIC_PROJECT_NAME]

    /**
     * Given a CI environment, returns the branch name.
     */
    fun getBranchName(env: Map<String, String>): String? = env[EnvConstants.GENERIC_BRANCH_NAME]

    /**
     * Gets a suffix to apply to the default build name.
     */
    fun getBuildSuffix(env: Map<String, String>): String? = env[EnvConstants.GENERIC_BUILD_NUMBER]

    /**
     * Given a CI environment, returns the version of the build.
     *
     * It's typically made available through the VERSION environment variable.
     */
    fun getBuildVersion(env: Map<String, String>): String? = env[EnvConstants.GENERIC_BUILD_VERSION]

    /**
     * Checks if the given environment matches this CI engine.
     */
    fun matchesEnv(env: Map<String, String>): Boolean

    /**
     * Gets the SCM URL associated with this CI engine and this environment.
     *
     * @param env Environment to check
     * @return SCM URL or null if cannot be determined from the environment
     */
    fun getScmUrl(env: Map<String, String>): String? = null

    /**
     * Gets the SCM revision associated with this CI engine and this environment.
     */
    fun getScmRevision(env: Map<String, String>): String?

    /**
     * Configures a build with this CI engine.
     *
     * @param build Build to configure
     * @param configuration Configuration for the build
     * @param env Environment variables to use for the configuration
     */
    fun configureBuild(build: Build, configuration: BuildConfiguration, env: Map<String, String>)

    /**
     * ID of the engine
     */
    val name: String
}