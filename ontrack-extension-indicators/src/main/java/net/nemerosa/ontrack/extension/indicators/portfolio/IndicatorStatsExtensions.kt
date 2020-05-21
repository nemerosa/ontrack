package net.nemerosa.ontrack.extension.indicators.portfolio

import net.nemerosa.ontrack.extension.indicators.model.IndicatorCompliance
import net.nemerosa.ontrack.extension.indicators.model.Rating

fun trendBetween(previous: IndicatorCompliance?, current: IndicatorCompliance?): IndicatorTrend? =
        if (previous != null && current != null) {
            trendBetween(Rating.asRating(previous.value), Rating.asRating(current.value))
        } else {
            null
        }

fun trendBetween(previous: Rating, current: Rating): IndicatorTrend =
        when {
            current > previous -> IndicatorTrend.GROWTH
            current == previous -> IndicatorTrend.SAME
            else -> IndicatorTrend.DECREASE
        }
