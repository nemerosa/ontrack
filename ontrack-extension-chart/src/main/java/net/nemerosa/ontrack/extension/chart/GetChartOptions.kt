package net.nemerosa.ontrack.extension.chart

import com.fasterxml.jackson.annotation.JsonIgnore
import net.nemerosa.ontrack.extension.chart.support.Interval
import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("General options for getting some data for a chart")
data class GetChartOptions(
    @APIDescription("Interval of time for the chart data")
    val interval: String,
    @APIDescription("Period used to consolidate the chart data")
    val period: String,
) {
    /**
     * Gets the interval as some actual timestamps
     */
    @get:JsonIgnore
    val actualInterval: Interval by lazy {
        Interval.parse(interval)
    }
}