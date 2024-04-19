package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.PredefinedPromotionLevel
import net.nemerosa.ontrack.test.TestUtils
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PromotionLevelMutationsIT : AbstractQLKTITSupport() {

    @Test
    fun `Updating a promotion level must keep the predefined promotion level description`() {
        asAdmin {
            val plName = TestUtils.uid("pl-")
            val ppl = predefinedPromotionLevelService.newPredefinedPromotionLevel(
                PredefinedPromotionLevel.of(
                    NameDescription.nd(plName, "Description at predefined level")
                )
            )

            project {
                branch {
                    // Creating a promotion level
                    run(
                        """
                        mutation {
                            setupPromotionLevel(input: {
                                project: "${project.name}",
                                branch: "$name",
                                promotion: "$plName",
                                description: ""
                            }) {
                                promotionLevel {
                                    id
                                }
                            }
                        }
                    """
                    ) { data ->
                        val plId = ID.of(
                            data.path("setupPromotionLevel")
                                .path("promotionLevel")
                                .path("id")
                                .asInt()
                        )
                        // Predefined description must have been set
                        val pl1 = structureService.getPromotionLevel(plId)
                        assertEquals(ppl.description, pl1.description, "Initial description being set")
                        // Updating the promotion level without a description
                        run(
                            """
                        mutation {
                            setupPromotionLevel(input: {
                                project: "${project.name}",
                                branch: "$name",
                                promotion: "$plName",
                                description: ""
                            }) {
                                promotionLevel {
                                    id
                                }
                            }
                        }
                    """
                        ) {
                            // Predefined description must have been kept
                            val pl2 = structureService.getPromotionLevel(plId)
                            assertEquals(ppl.description, pl2.description, "Predefined description must have been kept")
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Reordering the promotion levels`() {
        asAdmin {
            project {
                branch {
                    promotionLevel("PLATINUM")
                    promotionLevel("GOLD")
                    promotionLevel("SILVER")
                    promotionLevel("BRONZE")

                    fun swap(oldName: String, newName: String) {
                        run(
                            """
                            mutation {
                                reorderPromotionLevelById(input: {
                                    branchId: $id,
                                    oldName: "$oldName",
                                    newName: "$newName",
                                }) {
                                    errors {
                                        message
                                    }
                                }
                            }
                        """
                        ) { data ->
                            checkGraphQLUserErrors(data, "reorderPromotionLevelById")
                        }
                    }

                    swap("BRONZE", "PLATINUM")
                    assertEquals(
                        listOf(
                            "BRONZE",
                            "GOLD",
                            "SILVER",
                            "PLATINUM",
                        ),
                        structureService.getPromotionLevelListForBranch(id).map { it.name }
                    )

                    swap("GOLD", "SILVER")
                    assertEquals(
                        listOf(
                            "BRONZE",
                            "SILVER",
                            "GOLD",
                            "PLATINUM",
                        ),
                        structureService.getPromotionLevelListForBranch(id).map { it.name }
                    )

                }
            }
        }
    }

}