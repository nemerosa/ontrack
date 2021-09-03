package net.nemerosa.ontrack.extension.indicators.portfolio

import net.nemerosa.ontrack.extension.indicators.AbstractIndicatorsTestSupport
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class IndicatorPortfolioIT : AbstractIndicatorsTestSupport() {

    @Test
    fun `Deleting a category removes it from a portfolio`() {
        val categories = (1..3).map { category() }
        // Creating a portfolio for these categories
        val portfolio = portfolio(
            categories = categories
        )
        // Deleting the first category
        val firstCategoryId = categories.first().id
        asAdmin {
            indicatorCategoryService.deleteCategory(firstCategoryId)
        }
        // Checks this category is gone
        assertNull(asAdmin { indicatorCategoryService.findCategoryById(firstCategoryId) }, "Category is gone")
        // Checks this category is gone from the portfolio
        asAdmin {
            assertNotNull(indicatorPortfolioService.findPortfolioById(portfolio.id)) {
                assertFalse(it.categories.contains(firstCategoryId), "Category is gone from portfolio")
            }
        }
    }

}