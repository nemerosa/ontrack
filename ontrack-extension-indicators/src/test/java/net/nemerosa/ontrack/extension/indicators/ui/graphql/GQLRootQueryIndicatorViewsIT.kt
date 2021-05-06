package net.nemerosa.ontrack.extension.indicators.ui.graphql

import net.nemerosa.ontrack.extension.indicators.AbstractIndicatorsTestSupport
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorView
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GQLRootQueryIndicatorViewsIT : AbstractIndicatorsTestSupport() {

    @Test
    fun `List of indicator views`() {
        // Categories & types
        val category1 = category(id = "cat-1")
        val category2 = category(id = "cat-2")
        // Indicator views
        val name = uid("V")
        asAdmin {
            indicatorViewService.saveIndicatorView(
                IndicatorView(
                    name = name,
                    categories = listOf(
                        category1.id,
                        category2.id
                    )
                )
            )
        }
        // Gets the views
        asAdmin {
            run(
                """{
                    indicatorViews {
                        name
                        categories {
                            id
                            name
                        }
                    }
                }"""
            ).let { data ->
                val view = data.path("indicatorViews").find { it.path("name").asText() == name }
                assertNotNull(view) {
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
