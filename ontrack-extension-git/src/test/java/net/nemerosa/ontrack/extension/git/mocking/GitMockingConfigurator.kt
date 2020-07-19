package net.nemerosa.ontrack.extension.git.mocking

import net.nemerosa.ontrack.extension.git.model.GitConfiguration
import net.nemerosa.ontrack.extension.git.model.GitConfigurator
import net.nemerosa.ontrack.extension.git.model.GitPullRequest
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

@Component
class GitMockingConfigurator(
        private val propertyService: PropertyService
) : GitConfigurator {

    override fun isProjectConfigured(project: Project): Boolean =
            propertyService.hasProperty(project, GitMockingConfigurationPropertyType::class.java)

    override fun getConfiguration(project: Project): GitConfiguration? =
            propertyService.getProperty(project, GitMockingConfigurationPropertyType::class.java)
                    .value
                    ?.let { GitMockingConfiguration() }

    override fun toPullRequestID(key: String): Int? {
        if (key.isNotBlank()) {
            val m = "#(\\d+)".toRegex().matchEntire(key)
            if (m != null) {
                return m.groupValues[1].toInt(10)
            }
        }
        return null
    }

    override fun getPullRequest(configuration: GitConfiguration, id: Int): GitPullRequest? =
            if (configuration is GitMockingConfiguration) {
                TODO("Creates a mock pull request")
            } else {
                null
            }
}