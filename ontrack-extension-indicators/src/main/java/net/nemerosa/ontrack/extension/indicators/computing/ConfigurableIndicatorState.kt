package net.nemerosa.ontrack.extension.indicators.computing

/**
 * Stored state for a [configurable indicator][ConfigurableIndicatorType].
 *
 * @property enabled Is this indicator enabled?
 * @property values Values for this indicator
 */
class ConfigurableIndicatorState(
    val enabled: Boolean,
    val values: List<ConfigurableIndicatorAttributeValue>
)