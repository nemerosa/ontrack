package net.nemerosa.ontrack.extension.indicators.ui.graphql

import net.nemerosa.ontrack.extension.indicators.AbstractIndicatorsTestSupport
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GQLRootQueryIndicatorPortfoliosIT : AbstractIndicatorsTestSupport() {

    @Test
    fun `Getting a portfolio by ID`() {
        val portfolio1 = portfolio()
        @Suppress("UNUSED_VARIABLE") val portfolio2 = portfolio()

        val data = asAdmin {
            run("""
                query LoadPortfolioOfPortfolios(${'$'}id: String!) {
                  indicatorPortfolios(id: ${'$'}id) {
                      id
                      name
                  }
                }
            """, mapOf("id" to portfolio1.id)
            )
        }

        val portfolios = data["indicatorPortfolios"]
        assertEquals(1, portfolios.size())
        val portfolio = portfolios[0]
        assertEquals(portfolio1.id, portfolio["id"].asText())
        assertEquals(portfolio1.name, portfolio["name"].asText())
    }

    @Test
    fun `Getting all portfolios`() {
        val portfolio1 = portfolio()
        val portfolio2 = portfolio()

        val data = asAdmin {
            run("""{
                  indicatorPortfolios {
                      id
                      name
                  }
            }""", mapOf("id" to portfolio1.id)
            )
        }

        val portfolios = data["indicatorPortfolios"]
        assertTrue(portfolios.any { it["id"].asText() == portfolio1.id })
        assertTrue(portfolios.any { it["id"].asText() == portfolio2.id })
    }

}
