package net.nemerosa.ontrack.extension.indicators.computing

/**
 * Value provided by an [IndicatorComputer] for a given [type] and project.
 */
data class IndicatorComputedValue<T, C>(
    val type: IndicatorComputedType<T, C>,
    val value: T?,
    val comment: String?
)