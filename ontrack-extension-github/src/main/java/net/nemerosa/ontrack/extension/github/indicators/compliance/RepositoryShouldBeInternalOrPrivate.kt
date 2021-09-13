package net.nemerosa.ontrack.extension.github.indicators.compliance

import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClientFactory
import net.nemerosa.ontrack.extension.github.model.GitHubRepositorySettings
import net.nemerosa.ontrack.extension.github.model.GitHubRepositoryVisibility
import net.nemerosa.ontrack.extension.indicators.computing.ConfigurableIndicatorAttribute
import net.nemerosa.ontrack.extension.indicators.computing.ConfigurableIndicatorState
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueType
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueTypeConfig
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

// @Component
// TODO Disabled until https://github.community/t/the-rest-api-for-getting-a-repository-does-not-return-the-visibility/195158 is fixed
class RepositoryShouldBeInternalOrPrivate(
    propertyService: PropertyService,
    clientFactory: OntrackGitHubClientFactory,
    valueType: BooleanIndicatorValueType
) : AbstractRepositorySettingsCheck(
    propertyService,
    clientFactory,
    valueType
) {
    override val id: String = ID
    override val name: String = "A repository {required} be internal or private"

    override val valueConfig = { _: Project, state: ConfigurableIndicatorState -> BooleanIndicatorValueTypeConfig(required = state.getRequiredAttribute()) }
    override val needsVisibility: Boolean = true

    override val attributes: List<ConfigurableIndicatorAttribute> = listOf(
        ConfigurableIndicatorAttribute.requiredFlag
    )

    override fun checkSettings(project: Project, settings: GitHubRepositorySettings): Boolean? =
        settings.visibility?.let { it == GitHubRepositoryVisibility.INTERNAL || it == GitHubRepositoryVisibility.PRIVATE }

    companion object {
        const val ID = "github-compliance-repository-should-be-internal-or-private"
    }
}