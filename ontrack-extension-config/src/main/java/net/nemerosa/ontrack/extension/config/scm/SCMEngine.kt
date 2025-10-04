package net.nemerosa.ontrack.extension.config.scm

interface SCMEngine {
    val name: String

    /**
     * Given a CI environment, returns the project name.
     */
    fun getProjectName(env: Map<String, String>): String? = null
}