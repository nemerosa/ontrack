package net.nemerosa.ontrack.extension.indicators.model

import net.nemerosa.ontrack.extension.indicators.AbstractIndicatorsTestSupport
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IndicatorIT : AbstractIndicatorsTestSupport() {

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

}