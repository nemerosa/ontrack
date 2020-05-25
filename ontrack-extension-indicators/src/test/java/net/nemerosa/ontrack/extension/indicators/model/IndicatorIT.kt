package net.nemerosa.ontrack.extension.indicators.model

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.indicators.AbstractIndicatorsTestSupport
import net.nemerosa.ontrack.json.asJson
import org.junit.Test
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class IndicatorIT : AbstractIndicatorsTestSupport() {

    @Test
    fun `Life cycle of indicators`() {
        val category = category()
        val type = category.booleanType()
        project {
            // No indicator yet
            assertIndicatorNoValue(type)
            // Sets an indicator
            indicator(type, true)
            assertIndicatorValueIs(type, true)
            // Updates the indicator
            indicator(type, false)
            assertIndicatorValueIs(type, false)
            // Deletes the indicator
            asAdmin {
                indicatorService.deleteProjectIndicator(project, type.id)
            }
            assertIndicatorNoValue(type)
        }
    }

    @Test
    fun `Previous indicator when there is none`() {
        val category = category()
        val type = category.booleanType()
        project {
            // Sets an indicator NOW
            indicator(type, true)
            // Gets a previous indicator
            asAdmin {
                indicatorService.getPreviousProjectIndicator(project, type).apply {
                    assertEquals(type.id, this.type.id)
                    assertNull(value, "No value is set")
                }
            }
        }
    }

    @Test
    fun `Previous indicator when there is one`() {
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
                indicatorService.getPreviousProjectIndicator(project, type).apply {
                    assertEquals(type.id, this.type.id)
                    assertEquals(false, value)
                }
            }
        }
    }

    @Test
    fun `Deleting a type deletes the associated indicators`() {
        val category = category()
        val type = category.booleanType()
        val type2 = category.booleanType()
        project {
            indicator(type, true)
            indicator(type2, false)
            // Checks the indicator is set
            assertIndicatorValue(type) { assertTrue(it) }
            assertIndicatorValue(type2) { assertFalse(it) }
            // Deletes the type
            asAdmin {
                indicatorTypeService.deleteType(type.id)
            }
            // Checks the indicator is not set any longer
            assertIndicatorNoValue(type)
            assertIndicatorValue(type2) { assertFalse(it) }
        }
    }

    @Test
    fun `Deleting a category deletes the associated indicators`() {
        val category = category()
        val type = category.booleanType()
        val type2 = category.booleanType()
        project {
            indicator(type, true)
            indicator(type2, false)
            // Checks the indicator is set
            assertIndicatorValue(type) { assertTrue(it) }
            assertIndicatorValue(type2) { assertFalse(it) }
            // Deletes the category
            asAdmin {
                indicatorCategoryService.deleteCategory(category.id)
            }
            // Checks the indicator is not set any longer
            assertIndicatorNoValue(type)
            assertIndicatorNoValue(type2)
        }
    }

    @Test
    fun `No comment in input`() {
        val category = category()
        val type = category.booleanType()
        project {
            asAdmin {
                indicatorService.updateProjectIndicator<Boolean>(this, type.id, mapOf(
                        "value" to "true"
                ).asJson())
                checkIndicator(type) { i ->
                    assertEquals(true, i.value)
                    assertNull(i.comment)
                }
            }
        }
    }

    @Test
    fun `Null comment in input`() {
        val category = category()
        val type = category.booleanType()
        project {
            asAdmin {
                indicatorService.updateProjectIndicator<Boolean>(this, type.id, mapOf(
                        "value" to "true",
                        "comment" to null
                ).asJson())
                checkIndicator(type) { i ->
                    assertEquals(true, i.value)
                    assertNull(i.comment)
                }
            }
        }
    }

    @Test
    fun `Blank comment in input`() {
        val category = category()
        val type = category.booleanType()
        project {
            asAdmin {
                indicatorService.updateProjectIndicator<Boolean>(this, type.id, mapOf(
                        "value" to "true",
                        "comment" to ""
                ).asJson())
                checkIndicator(type) { i ->
                    assertEquals(true, i.value)
                    assertEquals("", i.comment)
                }
            }
        }
    }

    @Test
    fun `Comment in input`() {
        val category = category()
        val type = category.booleanType()
        project {
            asAdmin {
                indicatorService.updateProjectIndicator<Boolean>(this, type.id, mapOf(
                        "value" to "true",
                        "comment" to "Some comment"
                ).asJson())
                checkIndicator(type) { i ->
                    assertEquals(true, i.value)
                    assertEquals("Some comment", i.comment)
                }
            }
        }
    }

}