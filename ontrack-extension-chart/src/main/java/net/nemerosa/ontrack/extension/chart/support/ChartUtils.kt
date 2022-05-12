package net.nemerosa.ontrack.extension.chart.support

object ChartUtils {

    fun percentageFromBoolean(value: Boolean) = if (value) {
        100.0
    } else {
        0.0
    }

}