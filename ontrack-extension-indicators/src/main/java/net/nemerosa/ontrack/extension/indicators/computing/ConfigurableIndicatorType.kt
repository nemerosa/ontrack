package net.nemerosa.ontrack.extension.indicators.computing

import net.nemerosa.ontrack.extension.indicators.model.IndicatorValueType
import net.nemerosa.ontrack.model.structure.Project

/**
 * Descriptor for a configurable type
 *
 * @property category Associated category
 * @property id Unique ID for this type
 * @property name Display name this type, including variables as defined for being used by the templating service.
 * @property valueType Type of value (see [IndicatorComputedType.valueType]
 * @property valueConfig Configuration of this type (see [IndicatorComputedType.valueConfig], computed from the project and its stored state
 * @property attributes List of attributes for this type
 * @property computing Function to compute the value for this indicator
 */
class ConfigurableIndicatorType<T, C>(
    val category: IndicatorComputedCategory,
    val id: String,
    val name: String,
    val valueType: IndicatorValueType<T, C>,
    val valueConfig: (project: Project, state: ConfigurableIndicatorState) -> C,
    val attributes: List<ConfigurableIndicatorAttribute>,
    private val computing: (project: Project, state: ConfigurableIndicatorState) -> T?
) {
    fun computeValue(project: Project, state: ConfigurableIndicatorState) = computing(project, state)
}