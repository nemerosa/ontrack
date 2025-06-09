package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.indicators.AbstractIndicatorsTestSupport
import net.nemerosa.ontrack.json.asJson
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProjectIndicatorServiceIT : AbstractIndicatorsTestSupport() {

    @Autowired
    private lateinit var projectIndicatorService: ProjectIndicatorService

    @Test
    fun `Life cycle of project indicators`() {
        val category = category()
        val type = category.booleanType()
        project project@{
            asAdmin {
                // No indicator yet
                projectIndicatorService.getProjectCategoryIndicators(id, false).apply {
                    assertTrue(isEmpty(), "No indicator yet")
                }
                // Sets an indicator
                projectIndicatorService.updateIndicator(id, type.id, mapOf(
                        "value" to "false"
                ).asJson())
                assertIndicatorValueIs(type, false)
                // Updates the indicator again
                projectIndicatorService.updateIndicator(id, type.id, mapOf(
                        "value" to "true"
                ).asJson())
                assertIndicatorValueIs(type, true)
                // Gets the list of indicators
                projectIndicatorService.getProjectCategoryIndicators(id, false).apply {
                    assertEquals(1, size)
                    first().apply {
                        assertEquals(category, this.category)
                        assertEquals(this@project, this.project)
                        assertEquals(1, this.indicators.size)
                        this.indicators.first().apply {
                            assertEquals(
                                    mapOf("value" to "true").asJson(),
                                    this.value
                            )
                        }
                    }
                }
                // Deletes the indicator
                projectIndicatorService.deleteIndicator(id, type.id)
                projectIndicatorService.getProjectCategoryIndicators(id, false).apply {
                    assertTrue(isEmpty(), "No indicator yet")
                }
            }
        }
    }

    @Test
    fun `Previous indicator`() {
        // Trend times
        val duration = Duration.ofDays(7)
        val lastTime = Time.now() - Duration.ofDays(1)
        val pastTime = lastTime - duration
        // Category & type
        val category = category()
        val type = category.booleanType()
        project {
            // Sets indicators
            indicator(type, false, pastTime)
            indicator(type, true, lastTime)
            // Gets a previous indicator
            asAdmin {
                val indicator = projectIndicatorService.getProjectCategoryIndicators(id, false).first().indicators.first()
                assertEquals(mapOf("value" to "true").asJson(), indicator.value)
                val previousIndicator = projectIndicatorService.getPreviousIndicator(indicator)
                assertEquals(this, previousIndicator.project)
                assertEquals(mapOf("value" to "false").asJson(), previousIndicator.value)
            }
        }
    }

}