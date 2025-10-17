package net.nemerosa.ontrack.extension.stash.config

import net.nemerosa.ontrack.extension.config.ci.engine.CIEngine
import net.nemerosa.ontrack.extension.config.model.BranchConfiguration
import net.nemerosa.ontrack.extension.config.model.BuildConfiguration
import net.nemerosa.ontrack.extension.config.model.ProjectConfiguration
import net.nemerosa.ontrack.extension.config.scm.AbstractSCMEngine
import net.nemerosa.ontrack.extension.config.scm.SCMEngineNoURLException
import net.nemerosa.ontrack.extension.git.config.GitSCMEngineHelper
import net.nemerosa.ontrack.extension.stash.model.BitbucketRepository
import net.nemerosa.ontrack.extension.stash.model.StashConfiguration
import net.nemerosa.ontrack.extension.stash.property.StashProjectConfigurationProperty
import net.nemerosa.ontrack.extension.stash.property.StashProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.stash.service.StashConfigurationService
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

@Component
class BitbucketServerSCMEngine(
    propertyService: PropertyService,
    private val stashConfigurationService: StashConfigurationService,
    private val gitSCMEngineHelper: GitSCMEngineHelper,
) : AbstractSCMEngine(
    propertyService = propertyService,
    name = "bitbucket-server",
) {

    /**
     * Matching if at least one configuration is matching the URL.
     */
    override fun matchesUrl(scmUrl: String): Boolean =
        findConfigurationByURL(scmUrl) != null

    override fun configureProject(
        project: Project,
        configuration: ProjectConfiguration,
        env: Map<String, String>,
        projectName: String,
        ciEngine: CIEngine
    ) {
        val scmUrl = ciEngine.getScmUrl(env) ?: throw SCMEngineNoURLException()
        val bbServerConfig = findConfigurationByURL(scmUrl)
            ?: throw BitbucketServerSCMUnexistingConfigException()
        val bbServerRepository = getRepository(scmUrl)
        val projectConfig = StashProjectConfigurationProperty(
            configuration = bbServerConfig,
            project = bbServerRepository.project,
            repository = bbServerRepository.repository,
            indexationInterval = configuration.scmIndexationInterval ?: 0,
            issueServiceConfigurationIdentifier = configuration.issueServiceIdentifier?.toRepresentation(),
        )
        val existingConfig =
            propertyService.getPropertyValue(project, StashProjectConfigurationPropertyType::class.java)
        if (existingConfig != projectConfig) {
            propertyService.editProperty(
                project,
                StashProjectConfigurationPropertyType::class.java,
                projectConfig
            )
        }
    }

    override fun configureBranch(
        branch: Branch,
        configuration: BranchConfiguration,
        env: Map<String, String>,
        scmBranch: String,
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

    private fun findConfigurationByURL(scmUrl: String): StashConfiguration? =
        stashConfigurationService.configurations.find {
            it.matchesUrl(scmUrl)
        }

    private fun getRepository(scmUrl: String): BitbucketRepository {
        val m = scmUrlRepoRegex.find(scmUrl)
        return if (m != null) {
            val project = m.groupValues[1]
            val repo = m.groupValues[2]
            BitbucketRepository(project, repo)
        } else {
            throw BitbucketServerSCMRepositoryNotDetectedException(scmUrl)
        }
    }

    companion object {
        private val scmUrlRepoRegex = "([^/:]*)/([^/]*)\\.git$".toRegex()
    }
}