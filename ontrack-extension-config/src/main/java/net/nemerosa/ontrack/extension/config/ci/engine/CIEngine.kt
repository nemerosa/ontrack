package net.nemerosa.ontrack.extension.config.ci.engine

interface CIEngine {

    /**
     * Given a CI environment, returns the project name.
     */
    fun getProjectName(env: Map<String, String>): String? = null

    /**
     * ID of the engine
     */
    val name: String
}