package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.json.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@AsAdminTest
class GQLBranchBuildFilterFieldsContributorIT : AbstractQLKTITSupport() {

    @Test
    fun `Getting the list of build filters for a branch without any`() {
        project {
            branch {
                run(
                    """
                    {
                        branches(id: $id) {
                            buildFilterResources {
                                isShared
                                name
                                type
                                data
                                error
                            }
                        }
                    }
                """
                ) { data ->
                    val branch = data.path("branches").path(0)
                    val filters = branch.path("buildFilterResources")
                    // There are none ny default
                    assertEquals(0, filters.size(), "No filter by default")
                }
            }
        }
    }

    @Test
    fun `Getting the list of build filters for a branch with shared filters`() {
        asAdmin {
            project {
                branch {
                    // Creates and saves a shared filter
                    buildFilterService.saveFilter(
                        id,
                        true,
                        "MyFilter",
                        "net.nemerosa.ontrack.service.StandardBuildFilterProvider",
                        mapOf(
                            "withPromotionLevel" to "IRON",
                        ).asJson(),
                    )
                    // Gets the list of filters
                    run(
                        """
                        {
                            branches(id: $id) {
                                buildFilterResources {
                                    isShared
                                    name
                                    type
                                    data
                                    error
                                }
                            }
                        }
                    """
                    ) { data ->
                        val branch = data.path("branches").path(0)
                        val filters = branch.path("buildFilterResources")
                        assertNotNull(filters.find {
                            it.getRequiredTextField("name") == "MyFilter"
                        }, "Found shared filter") { filter ->
                            assertEquals(true, filter.getRequiredBooleanField("isShared"))
                            assertEquals(
                                "net.nemerosa.ontrack.service.StandardBuildFilterProvider",
                                filter.getRequiredTextField("type")
                            )
                            assertEquals(
                                "IRON",
                                filter.getRequiredJsonField("data").getRequiredTextField("withPromotionLevel")
                            )
                            assertEquals(
                                """Promotion level IRON does not exist for filter "With promotion".""",
                                filter.getTextField("error")
                            )
                        }
                    }
                }
            }
        }
    }

}
