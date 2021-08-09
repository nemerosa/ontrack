package net.nemerosa.ontrack.extension.github.indicators.compliance

import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClientFactory
import net.nemerosa.ontrack.extension.github.model.GitHubRepositorySettings
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueType
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueTypeConfig
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

@Component
class RepositoryMustNotHaveTheWikiFeature(
    propertyService: PropertyService,
    clientFactory: OntrackGitHubClientFactory,
    valueType: BooleanIndicatorValueType,
) : AbstractRepositorySettingsCheck(
    propertyService,
    clientFactory,
    valueType
) {

    companion object {
        const val ID = "github-compliance-repository-must-not-have-the-wiki-feature"
    }

    override val id: String = ID
    override val name: String = "A repository MUST NOT have the Wiki feature"

    override val valueConfig = BooleanIndicatorValueTypeConfig(required = true)

    override fun checkSettings(project: Project, settings: GitHubRepositorySettings): Boolean? =
        settings.hasWikiEnabled

}