package net.nemerosa.ontrack.extension.github.ingestion.support

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.git.model.ConfiguredBuildGitCommitLink
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.git.support.GitCommitPropertyCommitLink
import net.nemerosa.ontrack.extension.github.ingestion.processing.*
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.PullRequest
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.getProjectName
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.normalizeName
import net.nemerosa.ontrack.extension.github.ingestion.settings.GitHubIngestionSettings
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationProperty
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.github.service.GitHubConfigurationService
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.support.NoConfig
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultIngestionModelAccessService(
    private val structureService: StructureService,
    private val gitHubConfigurationService: GitHubConfigurationService,
    private val propertyService: PropertyService,
    private val cachedSettingsService: CachedSettingsService,
    private val gitCommitPropertyCommitLink: GitCommitPropertyCommitLink,
) : IngestionModelAccessService {

    override fun getOrCreateProject(repository: Repository, configuration: String?): Project {
        val settings = cachedSettingsService.getCachedSettings(GitHubIngestionSettings::class.java)
        val name = getProjectName(
            owner = repository.owner.login,
            repository = repository.name,
            orgProjectPrefix = settings.orgProjectPrefix,
        )
        val project = structureService.findProjectByName(name)
            .getOrNull()
            ?: structureService.newProject(
                Project.of(
                    nd(
                        name = name,
                        description = repository.description,
                    )
                )
            )
        // Setup the Git configuration for this project
        setupProjectGitHubConfiguration(project, repository, settings, configuration)
        // OK
        return project
    }

    private fun setupProjectGitHubConfiguration(
        project: Project,
        repository: Repository,
        settings: GitHubIngestionSettings,
        configurationName: String?,
    ) {
        if (!propertyService.hasProperty(project, GitHubProjectConfigurationPropertyType::class.java)) {
            val configuration = if (!configurationName.isNullOrBlank()) {
                gitHubConfigurationService.findConfiguration(configurationName)
                    ?: throw GitHubConfigProvidedNameNotFoundException(configurationName)
            } else {
                // Gets the list of GH configs
                val configurations = gitHubConfigurationService.configurations
                // If no configuration, error
                if (configurations.isEmpty()) {
                    throw NoGitHubConfigException()
                }
                // If only 1 config, use it
                else if (configurations.size == 1) {
                    val candidate = configurations.first()
                    // Checks the URL
                    if (repository.htmlUrl.startsWith(candidate.url)) {
                        candidate
                    } else {
                        throw GitHubConfigURLMismatchException(repository.htmlUrl)
                    }
                }
                // If several configurations, select it based on the URL
                else {
                    val candidates = configurations.filter {
                        repository.htmlUrl.startsWith(it.url)
                    }
                    if (candidates.isEmpty()) {
                        throw GitHubConfigURLNoMatchException(repository.htmlUrl)
                    } else if (candidates.size == 1) {
                        candidates.first()
                    } else {
                        throw GitHubConfigURLSeveralMatchesException(repository.htmlUrl)
                    }
                }
            }
            // Project property if not already defined
            propertyService.editProperty(
                project,
                GitHubProjectConfigurationPropertyType::class.java,
                GitHubProjectConfigurationProperty(
                    configuration = configuration,
                    repository = repository.fullName,
                    indexationInterval = settings.indexationInterval,
                    issueServiceConfigurationIdentifier = settings.issueServiceIdentifier,
                )
            )
        }
    }

    override fun getOrCreateBranch(
        project: Project,
        headBranch: String,
        pullRequest: PullRequest?,
    ): Branch {
        val (branchName, gitBranch) = if (pullRequest != null) {
            val key = "PR-${pullRequest.number}"
            key to key
        } else {
            normalizeName(headBranch) to headBranch
        }
        val branch = structureService.findBranchByName(project.name, branchName)
            .getOrNull()
            ?: structureService.newBranch(
                Branch.of(
                    project,
                    nd(
                        name = branchName,
                        description = "$headBranch branch",
                    )
                )
            )
        // Setup the Git configuration for this branch
        if (!propertyService.hasProperty(branch, GitBranchConfigurationPropertyType::class.java)) {
            propertyService.editProperty(
                branch,
                GitBranchConfigurationPropertyType::class.java,
                GitBranchConfigurationProperty(
                    branch = gitBranch,
                    buildCommitLink = ConfiguredBuildGitCommitLink(
                        gitCommitPropertyCommitLink,
                        NoConfig.INSTANCE
                    ).toServiceConfiguration(),
                    isOverride = false,
                    buildTagInterval = 0,
                )
            )
        }
        // OK
        return branch
    }

}