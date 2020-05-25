package net.nemerosa.ontrack.extension.indicators.stats

import net.nemerosa.ontrack.extension.indicators.model.IndicatorCompliance

/**
 * Aggregation of statuses over several items.
 */
data class IndicatorStats(
        val total: Int,
        val count: Int,
        val min: IndicatorCompliance?,
        val avg: IndicatorCompliance?,
        val max: IndicatorCompliance?,
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

        fun compute(compliances: List<IndicatorCompliance?>): IndicatorStats {
            val actualStatuses = compliances.filterNotNull()
            val total = compliances.size
            val count = actualStatuses.size
            val min = actualStatuses.minBy { it.value }
            val max = actualStatuses.maxBy { it.value }
            val avg = if (count > 0) {
                actualStatuses.map { it.value }.average().toInt().let { IndicatorCompliance(it) }
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
