package net.nemerosa.ontrack.extension.indicators.ui.graphql

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.indicators.AbstractIndicatorsTestSupport
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorView
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GQLRootQueryIndicatorPortfoliosIT : AbstractIndicatorsTestSupport() {

    @Test
    fun `Getting a portfolio by ID`() {
        val portfolio1 = portfolio()
        @Suppress("UNUSED_VARIABLE") val portfolio2 = portfolio()

        val data = asAdmin {
            run(
                """
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
            run(
                """{
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

    @Test
    fun `Getting portfolio stats for a view`() {
        clearPortfolios()

        val category = category()
        val type = category.booleanType()
        val viewName = uid("V")
        asAdmin {
            indicatorViewService.saveIndicatorView(
                IndicatorView(
                    name = viewName,
                    categories = listOf(
                        category.id
                    )
                )
            )
        }

        val label = label()
        project {
            labels = listOf(label)
            indicator(type, false, Time.now())
        }

        val portfolio = portfolio(
            label = label
        )

        asAdmin {
            run(
                """query IndicatorPortfolios(
                ${'$'}viewName: String
            ) {
                indicatorPortfolios {
                    id
                    name
                    viewStats(name: ${'$'}viewName) {
                        category {
                          id
                        }
                        stats {
                          count
                          avg
                          avgRating
                        }
                    }
                }
            }""", mapOf("viewName" to viewName)
            ).let { data ->
                assertEquals(
                    mapOf(
                        "indicatorPortfolios" to listOf(
                            mapOf(
                                "id" to portfolio.id,
                                "name" to portfolio.name,
                                "viewStats" to listOf(
                                    mapOf(
                                        "category" to mapOf(
                                            "id" to category.id
                                        ),
                                        "stats" to mapOf(
                                            "count" to 1,
                                            "avg" to 0,
                                            "avgRating" to "F"
                                        )
                                    )
                                )
                            )
                        )
                    ).asJson(),
                    data
                )
            }
        }
    }

    @Test
    fun `Getting portfolio stats without a view`() {
        clearPortfolios()

        val category = category()
        val type = category.booleanType()

        val label = label()
        project {
            labels = listOf(label)
            indicator(type, false, Time.now())
        }

        val portfolio = portfolio(
            label = label
        )

        asAdmin {
            run(
                """query IndicatorPortfolios(
                ${'$'}viewName: String
            ) {
                indicatorPortfolios {
                    id
                    name
                    viewStats(name: ${'$'}viewName) {
                        category {
                          id
                        }
                        stats {
                          count
                          avg
                          avgRating
                        }
                    }
                }
            }""", mapOf("viewName" to null)
            ).let { data ->
                assertEquals(
                    mapOf(
                        "indicatorPortfolios" to listOf(
                            mapOf(
                                "id" to portfolio.id,
                                "name" to portfolio.name,
                                "viewStats" to null                            )
                        )
                    ).asJson(),
                    data
                )
            }
        }
    }

    @Test
    fun `Getting portfolio stats with a non-existent view`() {
        clearPortfolios()

        val category = category()
        val type = category.booleanType()

        val label = label()
        project {
            labels = listOf(label)
            indicator(type, false, Time.now())
        }

        val portfolio = portfolio(
            label = label
        )

        asAdmin {
            run(
                """query IndicatorPortfolios(
                ${'$'}viewName: String
            ) {
                indicatorPortfolios {
                    id
                    name
                    viewStats(name: ${'$'}viewName) {
                        category {
                          id
                        }
                        stats {
                          count
                          avg
                          avgRating
                        }
                    }
                }
            }""", mapOf("viewName" to uid("V"))
            ).let { data ->
                assertEquals(
                    mapOf(
                        "indicatorPortfolios" to listOf(
                            mapOf(
                                "id" to portfolio.id,
                                "name" to portfolio.name,
                                "viewStats" to null                            )
                        )
                    ).asJson(),
                    data
                )
            }
        }
    }

}
