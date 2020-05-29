package net.nemerosa.ontrack.extension.indicators.model

/**
 * History of a project indicator.
 */
class IndicatorHistory<T>(
        val items: List<Indicator<T>>,
        val offset: Int,
        val total: Int
)
