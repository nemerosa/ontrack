package net.nemerosa.ontrack.extension.indicators.portfolio

import net.nemerosa.ontrack.extension.indicators.model.IndicatorStatus

/**
 * Aggregation of statuses over several items.
 */
data class IndicatorStats(
        val total: Int,
        val count: Int,
        val min: IndicatorStatus?,
        val avg: IndicatorStatus?,
        val max: IndicatorStatus?,
        val minCount: Int,
        val maxCount: Int
) {

    init {
        check(total >= 0) { "Total must be >= 0" }
        check(count >= 0) { "Count must be >= 0" }
        check(total >= count) { "Total must be >= count" }
    }

    val percent: Int
        get() = if (total == 0) {
            0
        } else {
            (count * 100) / total
        }

    companion object {

        fun compute(statuses: List<IndicatorStatus?>): IndicatorStats {
            val actualStatuses = statuses.filterNotNull()
            val total = statuses.size
            val count = actualStatuses.size
            val min = actualStatuses.minBy { it.value }
            val max = actualStatuses.maxBy { it.value }
            val avg = if (count > 0) {
                actualStatuses.map { it.value }.average().toInt().let { IndicatorStatus(it) }
            } else {
                null
            }

            val minCount = actualStatuses.count { it.value == min?.value }
            val maxCount = actualStatuses.count { it.value == max?.value }

            return IndicatorStats(
                    total = total,
                    count = count,
                    min = min,
                    avg = avg,
                    max = max,
                    minCount = minCount,
                    maxCount = maxCount
            )
        }

    }

}
