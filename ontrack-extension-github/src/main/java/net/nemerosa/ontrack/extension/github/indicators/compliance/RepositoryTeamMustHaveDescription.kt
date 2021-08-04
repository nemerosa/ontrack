package net.nemerosa.ontrack.extension.github.indicators.compliance

import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClient
import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClientFactory
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationProperty
import net.nemerosa.ontrack.extension.indicators.computing.ConfigurableIndicatorAttribute
import net.nemerosa.ontrack.extension.indicators.computing.ConfigurableIndicatorAttributeType
import net.nemerosa.ontrack.extension.indicators.computing.ConfigurableIndicatorState
import net.nemerosa.ontrack.extension.indicators.computing.IndicatorComputedCategory
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueType
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueTypeConfig
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

@Component
class RepositoryTeamMustHaveDescription(
    propertyService: PropertyService,
    clientFactory: OntrackGitHubClientFactory,
    valueType: BooleanIndicatorValueType,
) : AbstractGitHubComplianceCheck<Boolean, BooleanIndicatorValueTypeConfig>(
    propertyService,
    clientFactory,
    valueType
) {

    companion object {
        const val REGEX_KEY = "regex"
    }

    override val category: IndicatorComputedCategory = GitHubComplianceCategories.structure
    override val id: String = "github-compliance-repository-team-must-have-description"
    override val name: String = "A repository team MUST have the {$REGEX_KEY} expression in its description"
    override val attributes: List<ConfigurableIndicatorAttribute> = listOf(
        ConfigurableIndicatorAttribute(
            key = REGEX_KEY,
            name = "Regular expression",
            type = ConfigurableIndicatorAttributeType.REGEX,
            required = true,
        )
    )

    override val valueConfig = BooleanIndicatorValueTypeConfig(required = true)

    override fun compute(
        project: Project,
        state: ConfigurableIndicatorState,
        config: GitHubProjectConfigurationProperty,
        client: OntrackGitHubClient
    ): Boolean? {
        // Gets the description
        val description = client.getRepositoryDescription(config.repository) ?: return null
        // Gets the regular expression
        val regexExpression = state.getAttribute(REGEX_KEY)?.takeIf { it.isNotBlank() } ?: ".+"
        val regex = regexExpression.toRegex()
        // Matching the regular expression
        return regex.matchEntire(description) != null
    }
}