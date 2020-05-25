package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.indicators.AbstractIndicatorsTestSupport
import net.nemerosa.ontrack.json.asJson
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
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
                projectIndicatorService.getProjectIndicators(id, false).apply {
                    assertTrue(categories.isEmpty(), "No indicator yet")
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
                projectIndicatorService.getProjectIndicators(id, false).apply {
                    assertEquals(1, categories.size)
                    categories.first().apply {
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
                projectIndicatorService.getProjectIndicators(id, false).apply {
                    assertTrue(categories.isEmpty(), "No indicator yet")
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
                val indicator = projectIndicatorService.getProjectIndicators(id, false).categories.first().indicators.first()
                assertEquals(mapOf("value" to "true").asJson(), indicator.value)
                val previousIndicator = projectIndicatorService.getPreviousIndicator(indicator)
                assertEquals(this, previousIndicator.project)
                assertEquals(mapOf("value" to "false").asJson(), previousIndicator.value)
            }
        }
    }

    @Test
    fun `Update form for a project indicator always adds the comment memo field`() {
        val category = category()
        val type = category.booleanType()
        project {
            indicator(type, value = true, comment = "Some comment")
            asAdmin {
                val form = projectIndicatorService.getUpdateFormForIndicator(id, type.id)
                // Checks the value field
                assertNotNull(form.getField("value")) { f ->
                    assertEquals("selection", f.type)
                    assertEquals("true", f.value)
                }
                // Checks the comment field
                assertNotNull(form.getField("comment")) { f ->
                    assertEquals("memo", f.type)
                    assertEquals("Some comment", f.value)
                }
            }
        }
    }

}