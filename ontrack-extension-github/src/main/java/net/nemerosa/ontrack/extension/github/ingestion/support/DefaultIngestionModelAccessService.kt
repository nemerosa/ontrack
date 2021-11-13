package net.nemerosa.ontrack.extension.github.ingestion.support

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.github.ingestion.processing.GitHubConfigURLMismatchException
import net.nemerosa.ontrack.extension.github.ingestion.processing.GitHubConfigURLNoMatchException
import net.nemerosa.ontrack.extension.github.ingestion.processing.GitHubConfigURLSeveralMatchesException
import net.nemerosa.ontrack.extension.github.ingestion.processing.NoGitHubConfigException
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
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultIngestionModelAccessService(
    private val structureService: StructureService,
    private val gitHubConfigurationService: GitHubConfigurationService,
    private val propertyService: PropertyService,
    private val cachedSettingsService: CachedSettingsService,
) : IngestionModelAccessService {

    override fun getOrCreateProject(repository: Repository): Project {
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
        setupProjectGitHubConfiguration(project, repository, settings)
        // OK
        return project
    }

    private fun setupProjectGitHubConfiguration(
        project: Project,
        repository: Repository,
        settings: GitHubIngestionSettings,
    ) {
        if (!propertyService.hasProperty(project, GitHubProjectConfigurationPropertyType::class.java)) {
            // Gets the list of GH configs
            val configurations = gitHubConfigurationService.configurations
            // If no configuration, error
            val configuration = if (configurations.isEmpty()) {
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
        baseBranch: String?,
    ): Branch {
        if (baseBranch != null) {
            TODO("Pull requests are not supported yet.")
        } else {
            val branchName = normalizeName(headBranch)
            return structureService.findBranchByName(project.name, branchName)
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
        }
    }

}