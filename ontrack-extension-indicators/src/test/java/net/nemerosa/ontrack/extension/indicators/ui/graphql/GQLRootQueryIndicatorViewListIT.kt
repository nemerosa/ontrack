package net.nemerosa.ontrack.extension.indicators.ui.graphql

import net.nemerosa.ontrack.extension.indicators.AbstractIndicatorsTestSupport
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorView
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GQLRootQueryIndicatorViewListIT : AbstractIndicatorsTestSupport() {

    @Test
    fun `List of indicator views`() {
        // Categories & types
        val category1 = category(id = "cat-1")
        val category2 = category(id = "cat-2")
        // Indicator views
        val name = uid("V")
        val id = asAdmin {
            indicatorViewService.saveIndicatorView(
                IndicatorView(
                    id = "",
                    name = name,
                    categories = listOf(
                        category1.id,
                        category2.id
                    )
                )
            ).id
        }
        // Gets the views
        asAdmin {
            run(
                """{
                    indicatorViewList {
                        views {
                            id
                            name
                            categories {
                                id
                                name
                            }
                        }
                    }
                }"""
            ).let { data ->
                val view = data.path("indicatorViewList").path("views").find { it.path("name").asText() == name }
                assertNotNull(view) {
                    assertEquals(id, it.path("id").asText())
                    assertEquals(
                        listOf(
                            mapOf(
                                "id" to category1.id,
                                "name" to category1.name
                            ),
                            mapOf(
                                "id" to category2.id,
                                "name" to category2.name
                            )
                        ).asJson(),
                        it.path("categories")
                    )
                }
            }
        }
    }

    @Test
    fun `Indicator view by ID`() {
        // Categories & types
        val category1 = category(id = "cat-1")
        val category2 = category(id = "cat-2")
        // Indicator views
        val name = uid("V")
        val id = asAdmin {
            indicatorViewService.saveIndicatorView(
                IndicatorView(
                    id = "",
                    name = name,
                    categories = listOf(
                        category1.id,
                        category2.id
                    )
                )
            ).id
        }
        // Gets the views
        asAdmin {
            run(
                """{
                    indicatorViewList {
                        views(id: "$id") {
                            id
                            name
                            categories {
                                id
                                name
                            }
                        }
                    }
                }"""
            ).let { data ->
                val view = data.path("indicatorViewList").path("views").find { it.path("name").asText() == name }
                assertNotNull(view) {
                    assertEquals(id, it.path("id").asText())
                    assertEquals(
                        listOf(
                            mapOf(
                                "id" to category1.id,
                                "name" to category1.name
                            ),
                            mapOf(
                                "id" to category2.id,
                                "name" to category2.name
                            )
                        ).asJson(),
                        it.path("categories")
                    )
                }
            }
        }
    }

}
