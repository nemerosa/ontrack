package net.nemerosa.ontrack.extension.github.indicators.compliance

import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClientFactory
import net.nemerosa.ontrack.extension.github.model.GitHubRepositorySettings
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
) : AbstractRepositorySettingsCheck(
    propertyService = propertyService,
    clientFactory = clientFactory,
    valueType = booleanIndicatorValueType,
) {

    companion object {
        const val ID = "github-compliance-repository-default-branch-should-be-main"
    }

    override val id: String = ID
    override val name: String = "A repository default branch SHOULD be main"

    override val valueConfig = BooleanIndicatorValueTypeConfig(required = false)

    override fun checkSettings(project: Project, settings: GitHubRepositorySettings): Boolean? =
        settings.defaultBranch == "main"

}