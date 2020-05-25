package net.nemerosa.ontrack.extension.indicators.portfolio

import net.nemerosa.ontrack.extension.indicators.AbstractIndicatorsTestSupport
import org.junit.Test

class IndicatorPortfolioIT: AbstractIndicatorsTestSupport() {

    @Test
    fun `Deleting a category removes it from a portfolio`() {
        val categories = (1..3).map { category() }
        val portfolio = portfolio(
                categories = categories
        )
    }

}