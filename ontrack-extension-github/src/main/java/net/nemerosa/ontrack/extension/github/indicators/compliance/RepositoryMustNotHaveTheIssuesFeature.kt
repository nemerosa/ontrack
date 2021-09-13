package net.nemerosa.ontrack.extension.github.indicators.compliance

import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClientFactory
import net.nemerosa.ontrack.extension.github.model.GitHubRepositorySettings
import net.nemerosa.ontrack.extension.indicators.computing.ConfigurableIndicatorState
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueType
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueTypeConfig
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

@Component
class RepositoryMustNotHaveTheIssuesFeature(
    propertyService: PropertyService,
    clientFactory: OntrackGitHubClientFactory,
    valueType: BooleanIndicatorValueType,
) : AbstractRepositorySettingsCheck(
    propertyService,
    clientFactory,
    valueType
) {

    companion object {
        const val ID = "github-compliance-repository-must-not-the-issues-feature"
    }

    override val id: String = ID
    override val name: String = "A repository MUST NOT have the issues feature"

    override val valueConfig = { _: Project, _: ConfigurableIndicatorState -> BooleanIndicatorValueTypeConfig(required = true) }

    override fun checkSettings(project: Project, settings: GitHubRepositorySettings): Boolean? =
        settings.hasIssuesEnabled
    
}