package net.nemerosa.ontrack.extension.chart

import com.fasterxml.jackson.annotation.JsonIgnore
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.chart.support.Interval
import net.nemerosa.ontrack.graphql.support.IgnoreRef
import net.nemerosa.ontrack.model.annotations.APIDescription
import java.time.LocalDateTime

@APIDescription("General options for getting some data for a chart")
data class GetChartOptions(
    @APIDescription("Reference data for computing the intervals (null for current time")
    val ref: LocalDateTime? = null,
    @APIDescription("Interval of time for the chart data")
    val interval: String,
    @APIDescription("Period used to consolidate the chart data")
    val period: String,
) {
    /**
     * Gets the interval as some actual timestamps
     */
    @get:JsonIgnore
    @IgnoreRef
    val actualInterval: Interval by lazy {
        Interval.parse(interval, ref ?: Time.now())
    }
}