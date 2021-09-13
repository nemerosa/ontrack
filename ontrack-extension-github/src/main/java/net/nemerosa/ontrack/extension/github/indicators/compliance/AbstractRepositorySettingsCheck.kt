package net.nemerosa.ontrack.extension.github.indicators.compliance

import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClient
import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClientFactory
import net.nemerosa.ontrack.extension.github.model.GitHubRepositorySettings
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationProperty
import net.nemerosa.ontrack.extension.indicators.computing.ConfigurableIndicatorAttribute
import net.nemerosa.ontrack.extension.indicators.computing.ConfigurableIndicatorState
import net.nemerosa.ontrack.extension.indicators.computing.IndicatorComputedCategory
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueType
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueTypeConfig
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService

abstract class AbstractRepositorySettingsCheck(
    propertyService: PropertyService,
    clientFactory: OntrackGitHubClientFactory,
    valueType: BooleanIndicatorValueType,
) : AbstractGitHubComplianceCheck<Boolean, BooleanIndicatorValueTypeConfig>(
    propertyService,
    clientFactory,
    valueType
) {

    final override val category: IndicatorComputedCategory = GitHubComplianceCategories.settings
    override val attributes: List<ConfigurableIndicatorAttribute> = emptyList()

    protected open val needsVisibility: Boolean = false

    final override fun compute(
        project: Project,
        state: ConfigurableIndicatorState,
        config: GitHubProjectConfigurationProperty,
        client: OntrackGitHubClient
    ): Boolean? {
        // Gets the settings
        val settings = client.getRepositorySettings(config.repository, askVisibility = needsVisibility)
        // Checks the settings
        return checkSettings(project, settings)
    }

    abstract fun checkSettings(project: Project, settings: GitHubRepositorySettings): Boolean?

}