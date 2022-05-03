package net.nemerosa.ontrack.extension.github.ingestion.support

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.general.ReleasePropertyType
import net.nemerosa.ontrack.extension.git.model.ConfiguredBuildGitCommitLink
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.git.support.GitCommitPropertyCommitLink
import net.nemerosa.ontrack.extension.github.ingestion.processing.*
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.IPullRequest
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.getProjectName
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.normalizeName
import net.nemerosa.ontrack.extension.github.ingestion.settings.GitHubIngestionSettings
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationProperty
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.github.service.GitHubConfigurationService
import net.nemerosa.ontrack.extension.github.workflow.BuildGitHubWorkflowRunPropertyType
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
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
        pullRequest: IPullRequest?,
    ): Branch {
        val (branchName, gitBranch) = if (pullRequest != null) {
            val key = "PR-${pullRequest.number}"
            key to key
        } else if (headBranch.startsWith(REFS_TAGS_PREFIX)) {
            error("Creating branch from tag is not supported: $headBranch")
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

    private fun findProjectFromRepository(repository: Repository): Project? {
        // Gets the general ingestion settings
        val settings = cachedSettingsService.getCachedSettings(GitHubIngestionSettings::class.java)
        // Gets the project name from the repository
        val projectName = getProjectName(
            owner = repository.owner.login,
            repository = repository.name,
            orgProjectPrefix = settings.orgProjectPrefix,
        )
        // Getting the project using its name
        return structureService.findProjectByName(projectName).getOrNull()
    }

    override fun findBuildByRunId(repository: Repository, runId: Long): Build? {
        // Gets the project
        val project = findProjectFromRepository(repository) ?: return null
        // Getting the build using its Run ID property
        return propertyService.findByEntityTypeAndSearchArguments(
            ProjectEntityType.BUILD,
            BuildGitHubWorkflowRunPropertyType::class,
            PropertySearchArguments(
                jsonContext = null,
                jsonCriteria = "(pp.json->>'runId')::bigint = :runId",
                criteriaParams = mapOf(
                    "runId" to runId,
                )
            )
        ).map {
            structureService.getBuild(it)
        }.firstOrNull {
            it.project.id == project.id
        }
    }

    override fun findBuildByBuildName(repository: Repository, buildName: String): Build? {
        // Gets the project
        val project = findProjectFromRepository(repository) ?: return null
        // Searching
        return structureService.buildSearch(
            projectId = project.id,
            form = BuildSearchForm(
                buildName = buildName,
                buildExactMatch = true,
            )
        ).firstOrNull { it.project.id == project.id }
    }

    override fun findBuildByBuildLabel(repository: Repository, buildLabel: String): Build? {
        // Gets the project
        val project = findProjectFromRepository(repository) ?: return null
        // Searching
        return structureService.buildSearch(
            projectId = project.id,
            form = BuildSearchForm(
                property = ReleasePropertyType::class.java.name,
                propertyValue = buildLabel,
            )
        ).firstOrNull { it.project.id == project.id }
    }
}