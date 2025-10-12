package net.nemerosa.ontrack.extension.github.config

import net.nemerosa.ontrack.extension.config.ci.engine.CIEngine
import net.nemerosa.ontrack.extension.config.model.BranchConfiguration
import net.nemerosa.ontrack.extension.config.model.BuildConfiguration
import net.nemerosa.ontrack.extension.config.model.ProjectConfiguration
import net.nemerosa.ontrack.extension.config.scm.AbstractSCMEngine
import net.nemerosa.ontrack.extension.config.scm.SCMEngineNoURLException
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationProperty
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.github.service.GitHubConfigurationService
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

@Component
class GitHubSCMEngine(
    propertyService: PropertyService,
    private val gitHubConfigurationService: GitHubConfigurationService,
) : AbstractSCMEngine(
    propertyService = propertyService,
    name = "github",
) {

    override fun configureProject(
        project: Project,
        configuration: ProjectConfiguration,
        env: Map<String, String>,
        projectName: String,
        ciEngine: CIEngine,
    ) {
        val scmUrl = ciEngine.getScmUrl(env) ?: throw SCMEngineNoURLException()
        val githubConfig = getGitHubConfig(configuration, scmUrl)
        val githubRepository = getGitHubRepository(scmUrl)
        val projectConfig = GitHubProjectConfigurationProperty(
            configuration = githubConfig,
            repository = githubRepository,
            indexationInterval = 0, // TODO
            issueServiceConfigurationIdentifier = configuration.issueServiceIdentifier?.toRepresentation(),
        )
        val existingConfig =
            propertyService.getPropertyValue(project, GitHubProjectConfigurationPropertyType::class.java)
        if (existingConfig != projectConfig) {
            propertyService.editProperty(
                project,
                GitHubProjectConfigurationPropertyType::class.java,
                projectConfig
            )
        }
    }

    internal fun getGitHubRepository(scmUrl: String): String =
        if (matchesUrl(scmUrl)) {
            val m = scmUrlRepoRegex.find(scmUrl)
            if (m != null) {
                val owner = m.groupValues[1]
                val repo = m.groupValues[2]
                "$owner/$repo"
            } else {
                throw GitHubSCMRepositoryNotDetectedException(scmUrl)
            }
        } else {
            throw GitHubSCMRepositoryNotDetectedException(scmUrl)
        }

    private fun getGitHubConfig(
        configuration: ProjectConfiguration,
        scmUrl: String
    ): GitHubEngineConfiguration {
        val scmConfig = configuration.scmConfig
        return if (scmConfig.isNullOrBlank()) {
            gitHubConfigurationService.configurations.find {
                scmUrl.startsWith(it.url)
            } ?: throw GitHubSCMUnexistingConfigException()
        } else {
            gitHubConfigurationService.getConfiguration(scmConfig)
        }
    }

    override fun configureBranch(
        branch: Branch,
        configuration: BranchConfiguration,
        env: Map<String, String>,
        scmBranch: String
    ) {
        TODO("Not yet implemented")
    }

    override fun configureBuild(
        build: Build,
        configuration: BuildConfiguration,
        env: Map<String, String>
    ) {
        TODO("Not yet implemented")
    }

    override fun matchesUrl(scmUrl: String): Boolean =
        scmUrl.startsWith(SCM_URL_HTTPS) ||
                scmUrl.startsWith(SCM_URL_SSH)

    companion object {
        const val SCM_URL_HTTPS = "https://github.com/"
        const val SCM_URL_SSH = "git@github.com:"

        private val scmUrlRepoRegex = "([^/:]*)/([^/]*)\\.git$".toRegex()
    }
}