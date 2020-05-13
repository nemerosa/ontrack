package net.nemerosa.ontrack.extension.indicators.model

import net.nemerosa.ontrack.extension.indicators.AbstractIndicatorsTestSupport
import org.junit.Test
import kotlin.test.assertTrue

class IndicatorTypeIT : AbstractIndicatorsTestSupport() {

    @Test
    fun `Deleting a category deletes the associated types`() {
        val category = category()
        val types = (1..3).map { category.booleanType() }
        val ids = types.map { it.id }

        // Checks we can find the types back
        assertTrue(ids.all { id ->
            indicatorTypeService.findTypeById(id) != null
        })

        // Deletes the category
        asAdmin {
            indicatorCategoryService.deleteCategory(category.id)
        }

        // Checks the types are gone
        assertTrue(ids.all { id ->
            indicatorTypeService.findTypeById(id) == null
        })
    }

}