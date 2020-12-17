package net.nemerosa.ontrack.extension.indicators.model

import net.nemerosa.ontrack.extension.indicators.AbstractIndicatorsTestSupport
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class IndicatorCategoryServiceIT : AbstractIndicatorsTestSupport() {

    @Test
    fun `Category cannot be deleted if there is a source and no deprecation reason`() {
        asAdmin {
            val category = category(source = source())
            assertFalse(indicatorCategoryService.deleteCategory(category.id).isSuccess, "Category was not deleted")
            assertNotNull(indicatorCategoryService.findCategoryById(category.id), "Category was not deleted")
        }
    }

    @Test
    fun `Category cannot be deleted if there is a source and a blank deprecation reason`() {
        asAdmin {
            val category = category(source = source(), deprecated = "")
            assertFalse(indicatorCategoryService.deleteCategory(category.id).isSuccess, "Category was not deleted")
            assertNotNull(indicatorCategoryService.findCategoryById(category.id), "Category was not deleted")
        }
    }

    @Test
    fun `Category can be deleted if there is a source but a deprecation reason`() {
        asAdmin {
            val category = category(source = source(), deprecated = "Because")
            assertTrue(indicatorCategoryService.deleteCategory(category.id).isSuccess, "Category was deleted")
            assertNull(indicatorCategoryService.findCategoryById(category.id), "Category has been deleted")
        }
    }

    @Test
    fun `Category can be deleted if there is no source`() {
        asAdmin {
            val category = category()
            assertTrue(indicatorCategoryService.deleteCategory(category.id).isSuccess, "Category was deleted")
            assertNull(indicatorCategoryService.findCategoryById(category.id), "Category has been deleted")
        }
    }

}