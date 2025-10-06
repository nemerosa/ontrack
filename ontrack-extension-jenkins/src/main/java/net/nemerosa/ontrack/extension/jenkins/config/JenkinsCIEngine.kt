package net.nemerosa.ontrack.extension.jenkins.config

import net.nemerosa.ontrack.extension.config.ci.engine.CIEngine
import net.nemerosa.ontrack.extension.config.model.EnvConstants
import org.springframework.stereotype.Component

@Component
class JenkinsCIEngine : CIEngine {

    override val name: String = "jenkins"

    override fun matchesEnv(env: Map<String, String>): Boolean =
        !env[JENKINS_URL].isNullOrBlank()

    override fun getProjectName(env: Map<String, String>): String? =
        super.getProjectName(env)
            ?: detectProjectName(env)

    private fun detectProjectName(env: Map<String, String>): String? {
        val gitUrl = env[EnvConstants.GIT_URL] ?: return null
        return gitUrlRegex.matchEntire(gitUrl)?.groupValues?.getOrNull(1)
    }

    companion object {
        const val JENKINS_URL = "JENKINS_URL"
        private val gitUrlRegex = """.*/([^/]+)\.git$""".toRegex()
    }

}