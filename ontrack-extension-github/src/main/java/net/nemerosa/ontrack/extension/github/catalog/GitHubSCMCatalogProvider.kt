package net.nemerosa.ontrack.extension.github.catalog

import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClientFactory
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationProperty
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.github.service.GitHubConfigurationService
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogEntry
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogProvider
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogSource
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogTeam
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

@Component
class GitHubSCMCatalogProvider(
    private val cachedSettingsService: CachedSettingsService,
    private val gitHubConfigurationService: GitHubConfigurationService,
    private val gitHubClientFactory: OntrackGitHubClientFactory,
    private val propertyService: PropertyService
) : SCMCatalogProvider {

    override val id: String = "github"

    override val entries: List<SCMCatalogSource>
        get() {
            val settings = cachedSettingsService.getCachedSettings(GitHubSCMCatalogSettings::class.java)
            return gitHubConfigurationService.configurations.flatMap { config ->
                getConfigEntries(settings, config)
            }
        }

    private fun getConfigEntries(
        settings: GitHubSCMCatalogSettings,
        config: GitHubEngineConfiguration
    ): Iterable<SCMCatalogSource> {
        val client = gitHubClientFactory.create(config)
        return client.organizations.filter { it.login in settings.orgs }.flatMap { org ->
            // Gets the list of teams for this organization
            val teams = client.getOrganizationTeams(org.login)
            // Indexing the repositories per team (repository name --> list of team slugs)
            val repositoryTeamIndex = mutableMapOf<String, MutableSet<SCMCatalogTeam>>()
            teams?.forEach { team ->
                val repositoryPermissions = client.getTeamRepositories(org.login, team.slug)
                repositoryPermissions?.forEach { (repository, permission) ->
                    repositoryTeamIndex.getOrPut(repository) { mutableSetOf() }.apply {
                        add(
                            SCMCatalogTeam(
                                id = team.slug,
                                name = team.name,
                                description = team.description,
                                url = team.html_url,
                                role = permission.name
                            )
                        )
                    }
                }
            }
            client.findRepositoriesByOrganization(org.login)
                .map { name -> "${org.login}/$name" }
                .map { repo ->
                    SCMCatalogSource(
                        config = config.name,
                        repository = repo,
                        repositoryPage = "${config.url}/$repo",
                        lastActivity = client.getRepositoryLastModified(repo),
                        teams = repositoryTeamIndex[repo]?.toList()
                    )
                }

        }
    }

    override fun matches(entry: SCMCatalogEntry, project: Project): Boolean {
        val property: GitHubProjectConfigurationProperty? =
            propertyService.getProperty(project, GitHubProjectConfigurationPropertyType::class.java).value
        return if (property != null) {
            property.configuration.name == entry.config &&
                    property.repository == entry.repository
        } else {
            false
        }
    }

}