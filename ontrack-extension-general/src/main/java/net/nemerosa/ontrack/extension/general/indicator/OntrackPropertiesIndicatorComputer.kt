package net.nemerosa.ontrack.extension.general.indicator

import net.nemerosa.ontrack.extension.general.GeneralExtensionFeature
import net.nemerosa.ontrack.extension.indicators.computing.*
import net.nemerosa.ontrack.extension.indicators.model.IndicatorSource
import net.nemerosa.ontrack.extension.indicators.model.IndicatorSourceProviderDescription
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueType
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueTypeConfig
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

@Component
class OntrackPropertiesIndicatorComputer(
    extensionFeature: GeneralExtensionFeature,
    private val structureService: StructureService,
    booleanIndicatorValueType: BooleanIndicatorValueType,
    configurableIndicatorService: ConfigurableIndicatorService,
) : AbstractConfigurableIndicatorComputer(extensionFeature, configurableIndicatorService) {

    override val name: String = "Ontrack properties"

    override val source = IndicatorSource(
        provider = IndicatorSourceProviderDescription("ontrack", "Ontrack"),
        name = "Ontrack properties"
    )

    /**
     * Parallelisation of the computation is not needed.
     */
    override val perProject: Boolean = false

    /**
     * All projects are eligible.
     */
    override fun isProjectEligible(project: Project): Boolean = true

    private val indicatorCategory = IndicatorComputedCategory(
        id = "ontrack-properties",
        name = "Ontrack properties",
    )

    override val configurableIndicators: List<ConfigurableIndicatorType<*, *>> = listOf(
        ConfigurableIndicatorType(
            category = indicatorCategory,
            id = "ontrack-builds-not-empty",
            name = "Ontrack projects {required} have at least one build",
            valueType = booleanIndicatorValueType,
            valueConfig = { _, state -> BooleanIndicatorValueTypeConfig(required = state.getRequiredAttribute()) },
            attributes = listOf(
                ConfigurableIndicatorAttribute.requiredFlag
            ),
            computing = { project, _ ->
                val count = structureService.getBuildCountForProject(project)
                count > 0
            }
        )
    )

}