package net.nemerosa.ontrack.extension.jenkins.config

import net.nemerosa.ontrack.extension.config.ci.engine.CIEngine
import net.nemerosa.ontrack.extension.config.model.BuildConfiguration
import net.nemerosa.ontrack.extension.config.model.EnvConstants
import net.nemerosa.ontrack.extension.jenkins.JenkinsBuildProperty
import net.nemerosa.ontrack.extension.jenkins.JenkinsBuildPropertyType
import net.nemerosa.ontrack.extension.jenkins.JenkinsConfiguration
import net.nemerosa.ontrack.extension.jenkins.JenkinsConfigurationService
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

@Component
class JenkinsCIEngine(
    private val propertyService: PropertyService,
    private val jenkinsConfigurationService: JenkinsConfigurationService,
) : CIEngine {

    override val name: String = "jenkins"

    override fun matchesEnv(env: Map<String, String>): Boolean =
        !env[JENKINS_URL].isNullOrBlank()

    override fun getProjectName(env: Map<String, String>): String? =
        super.getProjectName(env)
            ?: detectProjectName(env)

    override fun getScmUrl(env: Map<String, String>): String? = env[EnvConstants.GIT_URL]

    override fun getScmRevision(env: Map<String, String>): String? = env[EnvConstants.GIT_COMMIT]

    override fun configureBuild(
        build: Build,
        configuration: BuildConfiguration,
        env: Map<String, String>
    ) {
        val jenkinsUrl = env[JENKINS_URL] ?: return
        val jobName = env[JOB_NAME] ?: return
        val jenkinsConfiguration = findConfiguration(jenkinsUrl) ?: return
        val buildNumber = env[BUILD_NUMBER]?.toIntOrNull() ?: return
        propertyService.editProperty(
            entity = build,
            propertyType = JenkinsBuildPropertyType::class.java,
            data = JenkinsBuildProperty(
                configuration = jenkinsConfiguration,
                job = jobName,
                build = buildNumber,
            )
        )
    }

    private fun findConfiguration(jenkinsUrl: String): JenkinsConfiguration? =
        jenkinsConfigurationService.configurations.find {
            it.url.trimEnd('/') == jenkinsUrl.trimEnd('/')
        }

    private fun detectProjectName(env: Map<String, String>): String? {
        val gitUrl = env[EnvConstants.GIT_URL] ?: return null
        return gitUrlRegex.matchEntire(gitUrl)?.groupValues?.getOrNull(1)
    }

    companion object {
        const val JENKINS_URL = "JENKINS_URL"
        const val JOB_NAME = "JOB_NAME"
        const val BUILD_NUMBER = "BUILD_NUMBER"
        private val gitUrlRegex = """.*/([^/]+)\.git$""".toRegex()
    }

}