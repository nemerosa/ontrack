package net.nemerosa.ontrack.extension.github.indicators.compliance

import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClient
import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClientFactory
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationProperty
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.indicators.computing.ConfigurableIndicatorState
import net.nemerosa.ontrack.extension.indicators.model.IndicatorValueType
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService

abstract class AbstractGitHubComplianceCheck<T, C>(
    private val propertyService: PropertyService,
    private val clientFactory: OntrackGitHubClientFactory,
    final override val valueType: IndicatorValueType<T, C>,
) : GitHubComplianceCheck<T, C> {

    final override val computing: (project: Project, state: ConfigurableIndicatorState) -> T?
        get() = { project, state ->
            // Gets the GitHub configuration or returns no value
            val config: GitHubProjectConfigurationProperty? =
                propertyService.getProperty(project, GitHubProjectConfigurationPropertyType::class.java).value
            if (config != null) {
                // Gets the client
                val client = clientFactory.create(config.configuration)
                // Computation
                compute(project, state, config, client)
            } else {
                // No config ==> no value
                null
            }
        }

    protected abstract fun compute(
        project: Project,
        state: ConfigurableIndicatorState,
        config: GitHubProjectConfigurationProperty,
        client: OntrackGitHubClient
    ): T?
}