package net.nemerosa.ontrack.extension.config.ci.engine

interface CIEngine {

    /**
     * Given a CI environment, returns the project name.
     */
    fun getProjectName(env: Map<String, String>): String? = null

    /**
     * Given a CI environment, returns the branch name.
     */
    fun getBranchName(env: Map<String, String>): String? = env["BRANCH_NAME"]

    /**
     * Gets a suffix to apply to the default build name.
     */
    fun getBuildSuffix(env: Map<String, String>): String? = null

    /**
     * Given a CI environment, returns the version of the build.
     *
     * It's typically made available through the VERSION environment variable.
     */
    fun getBuildVersion(env: Map<String, String>): String? = null

    /**
     * ID of the engine
     */
    val name: String
}