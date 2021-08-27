package net.nemerosa.ontrack.extension.indicators.computing

/**
 * Link between a [configurable indicator][ConfigurableIndicatorType] and its [state][ConfigurableIndicatorState].
 */
class ConfigurableIndicatorTypeState<T, C>(
    val type: ConfigurableIndicatorType<T, C>,
    val state: ConfigurableIndicatorState?,
)
