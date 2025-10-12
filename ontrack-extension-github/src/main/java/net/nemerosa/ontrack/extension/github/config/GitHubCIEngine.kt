package net.nemerosa.ontrack.extension.github.config

import net.nemerosa.ontrack.extension.config.ci.engine.CIEngine
import org.springframework.stereotype.Component

@Component
class GitHubCIEngine : CIEngine {

    override val name: String = "github"

    override fun matchesEnv(env: Map<String, String>): Boolean = env[GITHUB_ACTIONS] == "true"

    override fun getProjectName(env: Map<String, String>): String? =
        super.getProjectName(env) ?: detectProjectName(env)

    override fun getBranchName(env: Map<String, String>): String? = super.getBranchName(env) ?: env[GITHUB_REF_NAME]

    override fun getBuildSuffix(env: Map<String, String>): String? =
        super.getBuildSuffix(env) ?: env[GITHUB_RUN_NUMBER]

    private fun detectProjectName(env: Map<String, String>): String? {
        val fullName = env[GITHUB_REPOSITORY] ?: return null
        return fullName.substringAfter("/")
    }

    companion object {
        // const val GITHUB_SERVER_URL = "GITHUB_SERVER_URL"
        const val GITHUB_REPOSITORY = "GITHUB_REPOSITORY"
        const val GITHUB_REF_NAME = "GITHUB_REF_NAME"
        const val GITHUB_RUN_NUMBER = "GITHUB_RUN_NUMBER"
        const val GITHUB_ACTIONS = "GITHUB_ACTIONS"
    }
}