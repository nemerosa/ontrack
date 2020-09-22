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

    override fun getPullRequest(configuration: GitConfiguration, id: Int): GitPullRequest? =
            if (configuration is GitMockingConfiguration) {
                pullRequests[id]
            } else {
                null
            }

    private val pullRequests = mutableMapOf<Int, GitPullRequest>()

    fun clearPullRequests() {
        pullRequests.clear()
    }

    /**
     * Registers a PR for testing purpose.
     *
     * Caution: this method and [clearPullRequests] are not meant to run in parallel
     */
    fun registerPullRequest(
            id: Int,
            source: String = "feature/TK-$id-feature",
            target: String = "release/1.0",
            title: String = "PR n°$id",
            status: String = "open"
    ) {
        val pr = GitPullRequest(
                id = id,
                key = "#$id",
                source = source,
                target = target,
                title = title,
                status = status,
                url = "uri:testing:web:git:pr:$id"
        )
        pullRequests[id] = pr
    }

    /**
     * Unregisters a PR for testing purpose.
     *
     * Caution: this method and [clearPullRequests] are not meant to run in parallel
     */
    fun unregisterPullRequest(id: Int) {
        pullRequests.remove(id)
    }
}