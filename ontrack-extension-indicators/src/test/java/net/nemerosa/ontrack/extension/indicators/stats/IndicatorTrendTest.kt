package net.nemerosa.ontrack.extension.indicators.stats

import net.nemerosa.ontrack.extension.indicators.model.IndicatorCompliance
import net.nemerosa.ontrack.extension.indicators.model.Rating
import org.junit.Test
import kotlin.test.assertEquals

class IndicatorTrendTest {

    @Test
    fun `Trend between ratings`() {
        assertEquals(IndicatorTrend.GROWTH, trendBetween(Rating.C, Rating.B))
        assertEquals(IndicatorTrend.SAME, trendBetween(Rating.B, Rating.B))
        assertEquals(IndicatorTrend.DECREASE, trendBetween(Rating.A, Rating.B))
    }

    @Test
    fun `Trend between compliances`() {

        assertEquals(null, trendBetween(null, null))
        assertEquals(null, trendBetween(IndicatorCompliance.HIGHEST, null))
        assertEquals(null, trendBetween(null, IndicatorCompliance.HIGHEST))

        assertEquals(IndicatorTrend.GROWTH, trendBetween(IndicatorCompliance.MEDIUM, IndicatorCompliance.HIGHEST))
        assertEquals(IndicatorTrend.SAME, trendBetween(IndicatorCompliance.MEDIUM, IndicatorCompliance.MEDIUM))
        assertEquals(IndicatorTrend.DECREASE, trendBetween(IndicatorCompliance.MEDIUM, IndicatorCompliance.LOWEST))
    }

}