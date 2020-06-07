package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.extension.indicators.acl.IndicatorPortfolioIndicatorManagement
import net.nemerosa.ontrack.extension.indicators.acl.IndicatorPortfolioManagement
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorPortfolioOfPortfolios
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecoratorTestSupport
import net.nemerosa.ontrack.ui.resource.Link
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class IndicatorPortfolioOfPortfoliosResourceDecoratorIT : AbstractResourceDecoratorTestSupport() {

    @Autowired
    private lateinit var decorator: IndicatorPortfolioOfPortfoliosResourceDecorator

    @Test
    fun `No link when not authorized`() {
        val portfolio = IndicatorPortfolioOfPortfolios(emptyList())
        asUser().call {
            portfolio.decorate(decorator) {
                assertLinkNotPresent(Link.UPDATE)
                assertLinkNotPresent("_globalIndicators")
            }
        }
    }

    @Test
    fun `Create link when authorized`() {
        val portfolio = IndicatorPortfolioOfPortfolios(emptyList())
        asUserWith<IndicatorPortfolioManagement> {
            portfolio.decorate(decorator) {
                assertLinkPresent(Link.CREATE)
                assertLinkNotPresent("_globalIndicators")
            }
        }
    }

    @Test
    fun `Global mgt link when authorized`() {
        val portfolio = IndicatorPortfolioOfPortfolios(emptyList())
        asUserWith<IndicatorPortfolioIndicatorManagement> {
            portfolio.decorate(decorator) {
                assertLinkNotPresent(Link.CREATE)
                assertLinkPresent("_globalIndicators")
            }
        }
    }

}