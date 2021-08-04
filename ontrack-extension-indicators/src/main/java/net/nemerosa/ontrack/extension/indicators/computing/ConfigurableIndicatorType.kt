package net.nemerosa.ontrack.extension.indicators.computing

/**
 * Descriptor for a configurable type
 *
 * @property category Associated category
 * @property id Unique ID for this type
 * @property name Display name this type
 * @property attributes List of attributes for this type
 */
class ConfigurableIndicatorType(
    val category: IndicatorComputedCategory,
    val id: String,
    val name: String,
    val attributes: List<ConfigurableIndicatorAttribute>,
)