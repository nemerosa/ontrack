package net.nemerosa.ontrack.extension.indicators.stats

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.indicators.AbstractIndicatorsTestSupport
import net.nemerosa.ontrack.extension.indicators.model.IndicatorCompliance
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class IndicatorStatsServiceIT : AbstractIndicatorsTestSupport() {

    @Autowired
    private lateinit var indicatorStatsService: IndicatorStatsService

    @Test
    fun `No stats when no information for a category and project`() {
        val category = category()
        project {
            val stats = indicatorStatsService.getStatsForCategoryAndProject(category, this, null)
            assertEquals(category, stats.category)
            assertNull(stats.previousStats, "No previous state")
            stats.stats.apply {
                assertEquals(0, total)
                assertEquals(0, count)
                assertNull(min)
                assertNull(avg)
                assertNull(max)
                assertEquals(0, minCount)
                assertEquals(0, maxCount)
            }
        }
    }

    @Test
    fun `Stats for a category and project`() {
        val category = category()
        val type1 = category.booleanType(required = true)
        val type2 = category.booleanType(required = true)
        val type3 = category.booleanType(required = false)
        project {
            // Setting some indicators
            indicator(type1, true)
            indicator(type2, false)
            indicator(type3, false)
            // Getting the stats
            val stats = indicatorStatsService.getStatsForCategoryAndProject(category, this, null)
            assertEquals(category, stats.category)
            assertNull(stats.previousStats, "No previous state")
            stats.stats.apply {
                assertEquals(3, total)
                assertEquals(3, count)
                assertEquals(IndicatorCompliance(0), min)
                assertEquals(IndicatorCompliance(50), avg)
                assertEquals(IndicatorCompliance(100), max)
                assertEquals(1, minCount)
                assertEquals(1, maxCount)
            }
        }
    }

    @Test
    fun `Stats for a category and project with one indicator missing`() {
        val category = category()
        val type1 = category.booleanType(required = true)
        val type2 = category.booleanType(required = true)
        val type3 = category.booleanType(required = false)
        project {
            // Setting some indicators
            indicator(type1, true)
            indicator(type3, false)
            // Getting the stats
            val stats = indicatorStatsService.getStatsForCategoryAndProject(category, this, null)
            assertEquals(category, stats.category)
            assertNull(stats.previousStats, "No previous state")
            stats.stats.apply {
                assertEquals(3, total)
                assertEquals(2, count)
                assertEquals(IndicatorCompliance(50), min)
                assertEquals(IndicatorCompliance(75), avg)
                assertEquals(IndicatorCompliance(100), max)
                assertEquals(1, minCount)
                assertEquals(1, maxCount)
            }
        }
    }

    @Test
    fun `Previous stats for a category and project`() {
        val category = category()
        val type = category.booleanType()
        project {
            val duration = Duration.ofDays(7)
            val lastTime = Time.now() - Duration.ofDays(1)
            val pastTime = lastTime - duration
            // Past value
            indicator(type, true, pastTime)
            // Recent value
            indicator(type, false, lastTime)
            // Getting the previous stats
            val stats = indicatorStatsService.getStatsForCategoryAndProject(category, this, duration)
            assertEquals(category, stats.category)
            assertNotNull(stats.previousStats, "Previous state is filled in") {
                assertEquals(1, it.stats.total)
                assertEquals(1, it.stats.count)
                assertEquals(IndicatorCompliance(100), it.stats.avg)
                assertEquals(IndicatorTrend.DECREASE, it.avgTrend)
                assertEquals(Duration.ofDays(7), it.period)
            }
            stats.stats.apply {
                assertEquals(1, total)
                assertEquals(1, count)
                assertEquals(IndicatorCompliance(0), avg)
            }
        }
    }

    @Test
    fun `Previous stats for a category and project are not filled in when not available`() {
        val category = category()
        val type = category.booleanType()
        project {
            val duration = Duration.ofDays(7)
            // Current value
            indicator(type, false)
            // Getting the previous stats
            val stats = indicatorStatsService.getStatsForCategoryAndProject(category, this, duration)
            assertEquals(category, stats.category)
            assertNotNull(stats.previousStats, "Previous state is filled in") {
                assertEquals(1, it.stats.total)
                assertEquals(0, it.stats.count)
                assertNull(it.stats.avg)
                assertNull(it.avgTrend)
                assertEquals(Duration.ofDays(7), it.period)
            }
            stats.stats.apply {
                assertEquals(1, total)
                assertEquals(1, count)
                assertEquals(IndicatorCompliance(0), avg)
            }
        }
    }

    @Test
    fun `Stats for portfolio`() {
        // Categories & types
        val category1 = category()
        val type11 = category1.booleanType()
        val type12 = category1.booleanType()
        val category2 = category()
        val type21 = category2.booleanType()
        val type22 = category2.booleanType()
        // Label to use
        val label = label()
        // Portfolio definition
        val portfolio = portfolio(
                categories = listOf(category1, category2),
                label = label
        )
        // Projects, labels & indicator values
        project {
            labels = listOf(label)
            indicator(type11, false)
            indicator(type12, false)
            indicator(type21, false)
            indicator(type22, true)
        }
        project {
            labels = listOf(label)
            indicator(type11, true)
            indicator(type12, true)
            indicator(type21, false)
            indicator(type22, true)
        }
        project {
            labels = listOf(label)
            // Partial indicators only
            indicator(type11, true)
            indicator(type21, false)
        }
        // Gets the stats for this portfolio
        val categoryStats = indicatorStatsService.getStatsPortfolio(portfolio)
        assertEquals(2, categoryStats.size)
        // First category
        categoryStats[0].apply {
            assertEquals(category1, category)
            assertNull(previousStats)
            assertEquals(6, stats.total)
            assertEquals(5, stats.count)
            assertEquals(0, stats.min?.value)
            assertEquals(60, stats.avg?.value)
            assertEquals(100, stats.max?.value)
            assertEquals(2, stats.minCount)
            assertEquals(3, stats.maxCount)
        }
        // Second category
        categoryStats[1].apply {
            assertEquals(category2, category)
            assertNull(previousStats)
            assertEquals(6, stats.total)
            assertEquals(5, stats.count)
            assertEquals(0, stats.min?.value)
            assertEquals(40, stats.avg?.value)
            assertEquals(100, stats.max?.value)
            assertEquals(3, stats.minCount)
            assertEquals(2, stats.maxCount)
        }
    }

}