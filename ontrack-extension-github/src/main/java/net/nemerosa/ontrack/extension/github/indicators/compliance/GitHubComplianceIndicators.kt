package net.nemerosa.ontrack.extension.github.indicators.compliance

import net.nemerosa.ontrack.extension.github.GitHubExtensionFeature
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.indicators.computing.AbstractConfigurableIndicatorComputer
import net.nemerosa.ontrack.extension.indicators.computing.ConfigurableIndicatorService
import net.nemerosa.ontrack.extension.indicators.computing.ConfigurableIndicatorType
import net.nemerosa.ontrack.extension.indicators.model.IndicatorSource
import net.nemerosa.ontrack.extension.indicators.model.IndicatorSourceProviderDescription
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

/**
 * List of indicators checking if a project complies with some GitHub rules.
 */
@Component
class GitHubComplianceIndicators(
    extensionFeature: GitHubExtensionFeature,
    configurableIndicatorService: ConfigurableIndicatorService,
    private val propertyService: PropertyService,
    private val gitHubComplianceChecks: List<GitHubComplianceCheck<*, *>>,
) : AbstractConfigurableIndicatorComputer(extensionFeature, configurableIndicatorService) {

    override val name: String = "GitHub Compliance"

    /**
     * Evaluates all projects in one unique job
     */
    override val perProject: Boolean = false

    override val source = IndicatorSource(
        IndicatorSourceProviderDescription("github", "GitHub"),
        "Compliance"
    )

    override val configurableIndicators: List<ConfigurableIndicatorType<*, *>>
        get() = gitHubComplianceChecks.map { it.toConfigurableIndicatorType() }

    override fun isProjectEligible(project: Project): Boolean =
        propertyService.hasProperty(project, GitHubProjectConfigurationPropertyType::class.java)

}