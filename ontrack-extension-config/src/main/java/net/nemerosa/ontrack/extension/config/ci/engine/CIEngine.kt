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
     * ID of the engine
     */
    val name: String
}