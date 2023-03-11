package net.nemerosa.ontrack.extension.chart.support

import net.nemerosa.ontrack.extension.chart.Chart
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import java.time.LocalDateTime

class MetricsChart(
    val dates: List<String>,
    val metricNames: List<String>,
    val metricValues: List<Map<String, Double>>,
) : Chart {

    companion object {
        const val TYPE = "metrics"

        fun compute(
            items: List<MetricsChartItemData>,
            interval: Interval,
            period: String,
        ): MetricsChart {
            // Period based formatting
            val intervalPeriod = parseIntervalPeriod(period)
            // Gets all the intervals for the given period
            val intervals = interval.split(intervalPeriod)

            //

            // Group the metrics per interval and take the average
            val metricNames = mutableSetOf<String>()
            val metricValues = intervals.map { internalInterval ->
                val itemsInInterval = items.filter { item ->
                    item.timestamp in internalInterval
                }.map { it.metrics }
                // Values per metrics name
                val metricsValues = mutableMapOf<String, MutableList<Double>>()
                itemsInInterval.forEach { metrics ->
                    metrics.forEach { (metric, value) ->
                        metricNames.add(metric)
                        metricsValues
                            .getOrPut(metric) { mutableListOf() }
                            .add(value)
                    }
                }
                // Getting the average per metric
                metricsValues.mapValues { (metric, values) ->
                    val stats = DescriptiveStatistics()
                    values.forEach { stats.addValue(it) }
                    stats.mean
                }
            }

            // Chart
            return MetricsChart(
                dates = intervals.map {
                    intervalPeriod.format(it.start)
                },
                metricNames = metricNames.sorted(),
                metricValues = metricValues,
            )
        }
    }

}

data class MetricsChartItemData(
    val timestamp: LocalDateTime,
    val metrics: Map<String, Double>,
)