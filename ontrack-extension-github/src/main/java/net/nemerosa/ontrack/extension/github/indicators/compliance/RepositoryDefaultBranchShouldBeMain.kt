package net.nemerosa.ontrack.extension.github.indicators.compliance

import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClient
import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClientFactory
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationProperty
import net.nemerosa.ontrack.extension.indicators.computing.ConfigurableIndicatorAttribute
import net.nemerosa.ontrack.extension.indicators.computing.ConfigurableIndicatorState
import net.nemerosa.ontrack.extension.indicators.computing.IndicatorComputedCategory
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueType
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueTypeConfig
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

@Component
class RepositoryDefaultBranchShouldBeMain(
    booleanIndicatorValueType: BooleanIndicatorValueType,
    propertyService: PropertyService,
    clientFactory: OntrackGitHubClientFactory,
) : AbstractGitHubComplianceCheck<Boolean, BooleanIndicatorValueTypeConfig>(
    propertyService = propertyService,
    clientFactory = clientFactory,
    valueType = booleanIndicatorValueType,
) {

    override val category: IndicatorComputedCategory = GitHubComplianceCategories.settings
    override val id: String = "github-compliance-repository-default-branch-should-be-main"
    override val name: String = "A repository default branch SHOULD be main"
    override val attributes: List<ConfigurableIndicatorAttribute> = emptyList()

    override val valueConfig = BooleanIndicatorValueTypeConfig(required = false)

    override fun compute(
        project: Project,
        state: ConfigurableIndicatorState,
        config: GitHubProjectConfigurationProperty,
        client: OntrackGitHubClient
    ): Boolean? {
        val defaultBranch = client.getDefaultBranch(config.repository)
        return defaultBranch?.let { it == "main" }
    }
}