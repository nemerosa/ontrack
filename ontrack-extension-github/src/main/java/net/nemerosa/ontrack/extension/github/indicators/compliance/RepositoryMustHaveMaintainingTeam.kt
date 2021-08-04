package net.nemerosa.ontrack.extension.github.indicators.compliance

import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClient
import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClientFactory
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationProperty
import net.nemerosa.ontrack.extension.indicators.computing.ConfigurableIndicatorAttribute
import net.nemerosa.ontrack.extension.indicators.computing.ConfigurableIndicatorState
import net.nemerosa.ontrack.extension.indicators.computing.IndicatorComputedCategory
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueType
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueTypeConfig
import net.nemerosa.ontrack.extension.scm.catalog.CatalogLinkService
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

@Component
class RepositoryMustHaveMaintainingTeam(
    propertyService: PropertyService,
    clientFactory: OntrackGitHubClientFactory,
    valueType: BooleanIndicatorValueType,
    private val catalogLinkService: CatalogLinkService
) : AbstractGitHubComplianceCheck<Boolean, BooleanIndicatorValueTypeConfig>(
    propertyService,
    clientFactory,
    valueType
) {
    override val category: IndicatorComputedCategory = GitHubComplianceCategories.structure
    override val id: String = "github-compliance-repository-must-have-maintaining-team"
    override val name: String = "A repository MUST be assigned to at least one team"
    override val attributes: List<ConfigurableIndicatorAttribute> = emptyList()

    override val valueConfig = BooleanIndicatorValueTypeConfig(required = true)

    override fun compute(
        project: Project,
        state: ConfigurableIndicatorState,
        config: GitHubProjectConfigurationProperty,
        client: OntrackGitHubClient
    ): Boolean? {
        // Gets an entry from the SCM
        val entry = catalogLinkService.getSCMCatalogEntry(project) ?: return null
        // Gets the list of teams
        val teams = entry.teams ?: return null
        // Checks that at least one of them has a "maintains" role
        return teams.any { team -> team.role == "MAINTAIN" }
    }
}