package net.nemerosa.ontrack.extension.jenkins.indicator

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.indicators.computing.*
import net.nemerosa.ontrack.extension.indicators.model.IndicatorSource
import net.nemerosa.ontrack.extension.indicators.model.IndicatorSourceProviderDescription
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueType
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueTypeConfig
import net.nemerosa.ontrack.extension.jenkins.JenkinsExtensionFeature
import net.nemerosa.ontrack.extension.scm.service.SCMServiceDetector
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.templating.TemplatingService
import org.springframework.stereotype.Component

/**
 * Indicator which checks if a project default branch has a `Jenkinsfile` at its root.
 */
@Component
class JenkinsPipelineFileIndicatorComputer(
    extensionFeature: JenkinsExtensionFeature,
    private val scmServiceDetector: SCMServiceDetector,
    booleanIndicatorValueType: BooleanIndicatorValueType,
    configurableIndicatorService: ConfigurableIndicatorService,
    templatingService: TemplatingService,
) : AbstractConfigurableIndicatorComputer(extensionFeature, configurableIndicatorService, templatingService) {

    companion object {
        const val TYPE = "jenkins-pipeline-file"
    }

    /**
     * Parallelisation of the computation is not necessary for this indicator.
     */
    override val perProject: Boolean = false

    override val name: String = "Jenkins pipeline file"

    override val source: IndicatorSource = IndicatorSource(
        IndicatorSourceProviderDescription("jenkins-pipeline-file", "Jenkinsfile indicator"),
        "Jenkinsfile"
    )

    private val indicatorCategory = IndicatorComputedCategory(
        id = "jenkins-pipeline-file",
        name = "Jenkins pipeline file"
    )

    override fun isProjectEligible(project: Project): Boolean {
        val scmService = scmServiceDetector.getScmService(project).getOrNull()
        return scmService != null
    }

    override val configurableIndicators: List<ConfigurableIndicatorType<*, *>> = listOf(
        ConfigurableIndicatorType(
            category = indicatorCategory,
            id = TYPE,
            name = "The repository {required} have a Jenkinsfile",
            valueType = booleanIndicatorValueType,
            valueConfig = { _, state -> BooleanIndicatorValueTypeConfig(required = state.getRequiredAttribute()) },
            attributes = listOf(
                ConfigurableIndicatorAttribute.requiredFlag
            ),
            computing = { project, _ ->
                val scmService = scmServiceDetector.getScmService(project).getOrNull()
                if (scmService != null) {
                    // Gets the default branch
                    val defaultBranch = scmService.getSCMDefaultBranch(project)
                    if (defaultBranch != null) {
                        // Gets the content of the Jenkinsfile (or returns no indicator)
                        val jenkinsfile = scmService.download(project, defaultBranch, "Jenkinsfile")
                        // Presence
                        jenkinsfile != null && jenkinsfile.isNotBlank()
                    } else {
                        null
                    }
                } else {
                    null
                }
            }
        )
    )

}