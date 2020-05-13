package net.nemerosa.ontrack.extension.indicators.ui.graphql

import net.nemerosa.ontrack.json.asJson
import org.junit.Test
import kotlin.test.assertEquals

class GraphQLProjectIndicatorsIT : AbstractIndicatorsTestSupport() {

    @Test
    fun `Project category stats for one category`() {
        clearIndicators()

        val category = category()

        val type1 = category.booleanType()
        val type2 = category.booleanType()

        project {
            indicator(type1, true)
            indicator(type2, false)

            val data = run("""{
                projects(id: $id) {
                  projectIndicators {
                    categories {
                      categoryStats {
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
                  }
                }
            }""")

            assertEquals(
                    mapOf(
                            "projects" to listOf(
                                    mapOf(
                                            "projectIndicators" to mapOf(
                                                    "categories" to listOf(
                                                            mapOf(
                                                                    "categoryStats" to mapOf(
                                                                            "category" to mapOf(
                                                                                    "id" to category.id
                                                                            ),
                                                                            "stats" to mapOf(
                                                                                    "count" to 2,
                                                                                    "avg" to 50,
                                                                                    "avgRating" to "D"
                                                                            )
                                                                    )
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

    @Test
    fun `Project category stats`() {
        clearIndicators()

        val services = category()
        val delivery = category()
        val packaging = category()

        val type1 = services.booleanType()
        val type2 = delivery.booleanType()
        val type3 = packaging.booleanType()
        val type4 = packaging.booleanType()

        project {
            indicator(type1, true)
            indicator(type2, true)
            indicator(type3, false)
            indicator(type4, true)

            val data = run("""{
                projects(id: $id) {
                  projectIndicators {
                    categories {
                      categoryStats {
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
                  }
                }
            }""")

            assertEquals(
                    mapOf(
                            "projects" to listOf(
                                    mapOf(
                                            "projectIndicators" to mapOf(
                                                    "categories" to listOf(
                                                            mapOf(
                                                                    "categoryStats" to mapOf(
                                                                            "category" to mapOf(
                                                                                    "id" to services.id
                                                                            ),
                                                                            "stats" to mapOf(
                                                                                    "count" to 1,
                                                                                    "avg" to 100,
                                                                                    "avgRating" to "A"
                                                                            )
                                                                    )
                                                            ),
                                                            mapOf(
                                                                    "categoryStats" to mapOf(
                                                                            "category" to mapOf(
                                                                                    "id" to delivery.id
                                                                            ),
                                                                            "stats" to mapOf(
                                                                                    "count" to 1,
                                                                                    "avg" to 100,
                                                                                    "avgRating" to "A"
                                                                            )
                                                                    )
                                                            ),
                                                            mapOf(
                                                                    "categoryStats" to mapOf(
                                                                            "category" to mapOf(
                                                                                    "id" to packaging.id
                                                                            ),
                                                                            "stats" to mapOf(
                                                                                    "count" to 2,
                                                                                    "avg" to 50,
                                                                                    "avgRating" to "D"
                                                                            )
                                                                    )
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