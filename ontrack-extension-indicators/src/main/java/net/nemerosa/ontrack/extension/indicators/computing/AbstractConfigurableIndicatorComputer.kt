package net.nemerosa.ontrack.extension.indicators.computing

import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.extension.ExtensionFeature
import net.nemerosa.ontrack.model.structure.Project

/**
 * [IndicatorComputer] based on [configurable indicators][ConfigurableIndicatorType] only.
 */
abstract class AbstractConfigurableIndicatorComputer(
    extensionFeature: ExtensionFeature,
    private val configurableIndicatorService: ConfigurableIndicatorService,
) : AbstractExtension(extensionFeature), IndicatorComputer {

    override fun computeIndicators(project: Project): List<IndicatorComputedValue<*, *>> =
        configurableIndicators.mapNotNull { configurableIndicatorType ->
            computeIndicator(project, configurableIndicatorType)
        }

    private fun <T, C> computeIndicator(
        project: Project,
        configurableIndicatorType: ConfigurableIndicatorType<T, C>
    ): IndicatorComputedValue<T, C>? {
        // Gets the state of the configurable type
        val state = configurableIndicatorService.getConfigurableIndicatorState(configurableIndicatorType)
            ?.takeIf { it.enabled } // If indicator is not enabled, does ot compute any value
            ?: return null // Does not return any value if the configurable type has not been configured or is not enabled
        // Type of the indicator
        val type = IndicatorComputedType(
            category = configurableIndicatorType.category,
            id = configurableIndicatorType.id,
            name = configurableIndicatorType.expandName(state),
            link = state.link,
            valueType = configurableIndicatorType.valueType,
            valueConfig = configurableIndicatorType.valueConfig,
        )
        // Computes the value
        val value = configurableIndicatorType.computeValue(project, state)
        // OK
        return IndicatorComputedValue(
            type = type,
            value = value,
            comment = null,
        )
    }
}