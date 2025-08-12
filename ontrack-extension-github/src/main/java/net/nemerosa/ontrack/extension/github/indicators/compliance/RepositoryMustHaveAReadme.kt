package net.nemerosa.ontrack.extension.github.indicators.compliance

import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClient
import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClientFactory
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationProperty
import net.nemerosa.ontrack.extension.indicators.computing.ConfigurableIndicatorAttribute
import net.nemerosa.ontrack.extension.indicators.computing.ConfigurableIndicatorState
import net.nemerosa.ontrack.extension.indicators.computing.IndicatorComputedCategory
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueType
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueTypeConfig
import net.nemerosa.ontrack.extension.scm.service.SCMServiceDetector
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class RepositoryMustHaveAReadme(
    propertyService: PropertyService,
    clientFactory: OntrackGitHubClientFactory,
    valueType: BooleanIndicatorValueType,
    private val scmServiceDetector: SCMServiceDetector,
) : AbstractGitHubComplianceCheck<Boolean, BooleanIndicatorValueTypeConfig>(
    propertyService,
    clientFactory,
    valueType
) {

    companion object {
        const val ID = "github-compliance-repository-must-have-a-readme"
    }

    override val category: IndicatorComputedCategory = GitHubComplianceCategories.settings
    override val id: String = ID
    override val name: String = "The repository MUST have a README file"
    override val attributes: List<ConfigurableIndicatorAttribute> = emptyList()
    override val valueConfig = { _: Project, _: ConfigurableIndicatorState -> BooleanIndicatorValueTypeConfig(required = true) }

    override fun compute(
        project: Project,
        state: ConfigurableIndicatorState,
        config: GitHubProjectConfigurationProperty,
        client: OntrackGitHubClient,
    ): Boolean? {
        // Gets the default branch
        val branch = client.getRepositorySettings(config.repository).defaultBranch ?: return false
        // Gets the SCM service for this
        val scmService = scmServiceDetector.getScmService(project).getOrNull() ?: return false
        // Gets the README file
        val readme = scmService.download(project, branch, "README.md")
        // Checks the file
        return readme != null && readme.isNotEmpty()
    }
}