package net.nemerosa.ontrack.extension.indicators.ui.graphql

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.indicators.AbstractIndicatorsTestSupport
import net.nemerosa.ontrack.extension.indicators.portfolio.PortfolioGlobalIndicators
import net.nemerosa.ontrack.test.TestUtils
import org.junit.Test
import java.time.Duration
import kotlin.test.assertEquals

class GQLRootQueryIndicatorPortfolioOfPortfoliosIT : AbstractIndicatorsTestSupport() {

    @Test
    fun `List of all portfolios with their global stats and trends`() {
        clearIndicators()
        clearPortfolios()
        // Trend times
        val duration = Duration.ofDays(7)
        val lastTime = Time.now() - Duration.ofDays(1)
        val pastTime = lastTime - duration
        // Categories & types
        val category1 = category(id = "cat-1")
        val type11 = category1.booleanType(id = "type-11")
        val type12 = category1.booleanType(id = "type-12")
        val category2 = category(id = "cat-2")
        val type21 = category2.booleanType(id = "type-21")
        val type22 = category2.booleanType(id = "type-22")
        // Label to use
        val label = label(category = "test", name = "service", checkForExisting = true)
        // Portfolio definition
        portfolio(id = "P1", label = label)
        // Global portfolio categories
        asAdmin {
            indicatorPortfolioService.savePortfolioOfPortfolios(
                    PortfolioGlobalIndicators(listOf(category1.id, category2.id))
            )
        }
        // Projects, labels & indicator values
        project {
            labels = listOf(label)
            // Past
            indicator(type11, null, pastTime)
            indicator(type12, null, pastTime)
            indicator(type21, false, pastTime)
            indicator(type22, false, pastTime)
            // Current
            indicator(type11, false, lastTime)
            indicator(type12, false, lastTime)
            indicator(type21, false, lastTime)
            indicator(type22, true, lastTime)
        }
        project {
            labels = listOf(label)
            // Past
            indicator(type11, false, pastTime)
            indicator(type12, false, pastTime)
            indicator(type21, false, pastTime)
            indicator(type22, false, pastTime)
            // Current
            indicator(type11, true, lastTime)
            indicator(type12, true, lastTime)
            indicator(type21, false, lastTime)
            indicator(type22, true, lastTime)
        }
        project {
            labels = listOf(label)
            // Partial indicators only
            // Past
            indicator(type11, true, pastTime)
            indicator(type21, false, pastTime)
            // Current
            indicator(type11, true, lastTime)
            indicator(type21, false, lastTime)
        }

        // Querying the portfolio
        val data = asAdmin {
            run("""
                query LoadPortfolioOfPortfolios(${'$'}trendDuration: Int) {
                  indicatorPortfolioOfPortfolios {
                    portfolios {
                      id
                      name
                      label {
                        category
                        name
                        color
                        description
                      }
                      globalStats(duration: ${'$'}trendDuration) {
                        category {
                          id
                          name
                        }
                        stats {
                          total
                          count
                          min
                          minCount
                          minRating
                          avg
                          avgRating
                          max
                          maxCount
                          maxRating
                        }
                        previousStats {
                          stats {
                            avg
                            avgRating
                          }
                          avgTrend
                          durationSeconds
                        }
                      }
                    }
                  }
                }
            """, mapOf(
                    "trendDuration" to (duration.toMillis() / 1000).toInt()
            ))
        }

        val expected = TestUtils.resourceJson("/graphql-root-indicatorPortfolioOfPortfolios-result.json")

        assertEquals(expected, data)
    }

}
