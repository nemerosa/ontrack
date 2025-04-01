package net.nemerosa.ontrack.extension.indicators.model

import net.nemerosa.ontrack.extension.indicators.AbstractIndicatorsTestSupport
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import kotlin.test.*

class IndicatorCategoryServiceIT : AbstractIndicatorsTestSupport() {

    @Test
    fun `Category cannot be deleted if there is a source and no deprecation reason`() {
        asAdmin {
            val category = category(source = source())
            assertFalse(indicatorCategoryService.deleteCategory(category.id).success, "Category was not deleted")
            assertNotNull(indicatorCategoryService.findCategoryById(category.id), "Category was not deleted")
        }
    }

    @Test
    fun `Category cannot be deleted if there is a source and a blank deprecation reason`() {
        asAdmin {
            val category = category(source = source(), deprecated = "")
            assertFalse(indicatorCategoryService.deleteCategory(category.id).success, "Category was not deleted")
            assertNotNull(indicatorCategoryService.findCategoryById(category.id), "Category was not deleted")
        }
    }

    @Test
    fun `Category can be deleted if there is a source but a deprecation reason`() {
        asAdmin {
            val category = category(source = source(), deprecated = "Because")
            assertTrue(indicatorCategoryService.deleteCategory(category.id).success, "Category was deleted")
            assertNull(indicatorCategoryService.findCategoryById(category.id), "Category has been deleted")
        }
    }

    @Test
    fun `Category can be deleted if there is no source`() {
        asAdmin {
            val category = category()
            assertTrue(indicatorCategoryService.deleteCategory(category.id).success, "Category was deleted")
            assertNull(indicatorCategoryService.findCategoryById(category.id), "Category has been deleted")
        }
    }

    @Test
    fun `Finding categories by source`() {
        asAdmin {
            val source = IndicatorSource(
                provider = IndicatorSourceProviderDescription(uid("i"), uid("I")),
                name = uid("N")
            )
            val category = category(source = source)
            repeat(3) { category() } // Creates additional categories
            // Looking for the category using its source
            val categories = indicatorCategoryService.findBySource(source)
            assertEquals(listOf(category), categories)
        }
    }

}