package net.nemerosa.ontrack.extension.github.config

import net.nemerosa.ontrack.extension.config.ci.engine.CIEngine
import net.nemerosa.ontrack.extension.config.model.BranchConfiguration
import net.nemerosa.ontrack.extension.config.model.BuildConfiguration
import net.nemerosa.ontrack.extension.config.model.ProjectConfiguration
import net.nemerosa.ontrack.extension.config.scm.AbstractSCMEngine
import net.nemerosa.ontrack.extension.config.scm.SCMEngineNoURLException
import net.nemerosa.ontrack.extension.git.config.GitSCMEngineHelper
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
    private val gitSCMEngineHelper: GitSCMEngineHelper,
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
            indexationInterval = configuration.scmIndexationInterval ?: 0,
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
            findConfigurationByURL(scmUrl) ?: throw GitHubSCMUnexistingConfigException(scmUrl)
        } else {
            gitHubConfigurationService.getConfiguration(scmConfig)
        }
    }

    private fun findConfigurationByURL(scmUrl: String): GitHubEngineConfiguration? =
        gitHubConfigurationService.configurations.find {
            it.matchesUrl(scmUrl)
        }

    override fun configureBranch(
        branch: Branch,
        configuration: BranchConfiguration,
        env: Map<String, String>,
        scmBranch: String
    ) {
        gitSCMEngineHelper.configureBranch(
            branch = branch,
            scmBranch = scmBranch,
        )
    }

    override fun configureBuild(
        build: Build,
        configuration: BuildConfiguration,
        env: Map<String, String>,
        ciEngine: CIEngine,
    ) {
        val commit = ciEngine.getScmRevision(env)
        if (!commit.isNullOrBlank()) {
            gitSCMEngineHelper.configureBuild(
                build = build,
                commit = commit,
            )
        }
    }

    override fun matchesUrl(scmUrl: String): Boolean =
        findConfigurationByURL(scmUrl) != null

    companion object {
        private val scmUrlRepoRegex = "([^/:]*)/([^/]*)\\.git$".toRegex()
    }
}