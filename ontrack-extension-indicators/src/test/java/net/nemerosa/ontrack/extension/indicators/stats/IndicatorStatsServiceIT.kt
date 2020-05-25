package net.nemerosa.ontrack.extension.indicators.stats

import net.nemerosa.ontrack.extension.indicators.AbstractIndicatorsTestSupport
import net.nemerosa.ontrack.extension.indicators.model.IndicatorCompliance
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
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

}