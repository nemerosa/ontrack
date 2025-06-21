package net.nemerosa.ontrack.extension.indicators.computing

import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.events.PlainEventRenderer
import net.nemerosa.ontrack.model.extension.ExtensionFeature
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.templating.TemplatingService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * [IndicatorComputer] based on [configurable indicators][ConfigurableIndicatorType] only.
 */
abstract class AbstractConfigurableIndicatorComputer(
    extensionFeature: ExtensionFeature,
    private val configurableIndicatorService: ConfigurableIndicatorService,
    private val templatingService: TemplatingService,
) : AbstractExtension(extensionFeature), IndicatorComputer {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun computeIndicators(project: Project): List<IndicatorComputedValue<*, *>> =
        configurableIndicators.mapNotNull { configurableIndicatorType ->
            computeIndicator(project, configurableIndicatorType)
        }

    private fun <T, C> computeIndicator(
        project: Project,
        configurableIndicatorType: ConfigurableIndicatorType<T, C>
    ): IndicatorComputedValue<T, C>? {
        logger.info("[configurable-indicator] computer=${id},project=${project.name},start")
        // Gets the state of the configurable type
        val state = configurableIndicatorService.getConfigurableIndicatorState(configurableIndicatorType)
            ?.takeIf { it.enabled } // If indicator is not enabled, does ot compute any value
            .apply {
                logger.info("[configurable-indicator] computer=${id},project=${project.name},state=${this?.enabled}")
            }
            ?: return null // Does not return any value if the configurable type has not been configured or is not enabled
        // Type of the indicator
        val type = IndicatorComputedType(
            category = configurableIndicatorType.category,
            id = configurableIndicatorType.id,
            name = expandName(configurableIndicatorType, state),
            link = state.link,
            valueType = configurableIndicatorType.valueType,
            valueConfig = configurableIndicatorType.valueConfig(project, state),
        )
        // Computes the value
        logger.info("[configurable-indicator] computer=${id},project=${project.name},computing")
        val value = configurableIndicatorType.computeValue(project, state)
        // OK
        logger.info("[configurable-indicator] computer=${id},project=${project.name},value=${value},end")
        return IndicatorComputedValue(
            type = type,
            value = value,
            comment = null,
        )
    }

    private fun <C, T> expandName(
        configurableIndicatorType: ConfigurableIndicatorType<T, C>,
        state: ConfigurableIndicatorState
    ): String {
        val valueMap = state.values.associate {
            it.attribute.key to it.attribute.type.map(it.value)
        }
        return templatingService.render(
            template = configurableIndicatorType.name,
            context = valueMap,
            renderer = PlainEventRenderer.INSTANCE,
        )
    }
}