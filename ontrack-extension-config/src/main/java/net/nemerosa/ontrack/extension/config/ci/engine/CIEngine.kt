package net.nemerosa.ontrack.extension.config.ci.engine

import net.nemerosa.ontrack.extension.config.model.EnvConstants

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
     * ID of the engine
     */
    val name: String
}