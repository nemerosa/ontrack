package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.extension.indicators.acl.IndicatorPortfolioManagement
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorPortfolio
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecoratorTestSupport
import net.nemerosa.ontrack.ui.resource.Link
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class IndicatorPortfolioResourceDecoratorIT : AbstractResourceDecoratorTestSupport() {

    @Autowired
    private lateinit var decorator: IndicatorPortfolioResourceDecorator

    @Test
    fun `No link when not authorized`() {
        val portfolio = IndicatorPortfolio("test", "Test", null, emptyList())
        asUser().call {
            portfolio.decorate(decorator) {
                assertLinkNotPresent(Link.UPDATE)
                assertLinkNotPresent(Link.DELETE)
            }
        }
    }

    @Test
    fun `Links when authorized`() {
        val portfolio = IndicatorPortfolio("test", "Test", null, emptyList())
        asUserWith<IndicatorPortfolioManagement> {
            portfolio.decorate(decorator) {
                assertLinkPresent(Link.UPDATE)
                assertLinkPresent(Link.DELETE)
            }
        }
    }

}