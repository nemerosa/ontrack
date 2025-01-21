package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PredefinedPromotionLevelMutationsIT : AbstractQLKTITSupport() {

    @Test
    fun `Creating a predefined promotion level`() {
        asAdmin {
            val name = uid("ppl_")
            run(
                """
                    mutation {
                        createPredefinedPromotionLevel(input: {
                            name: "$name",
                            description: "Some text"
                        }) {
                            predefinedPromotionLevel {
                                id
                            }
                            errors {
                                message
                            }
                        }
                    }
                """
            ) { data ->
                checkGraphQLUserErrors(data, "createPredefinedPromotionLevel") { node ->
                    val id = node.path("predefinedPromotionLevel").path("id").asInt()
                    assertEquals(
                        name,
                        predefinedPromotionLevelService.getPredefinedPromotionLevel(ID.of(id)).name
                    )
                    assertEquals(
                        id,
                        predefinedPromotionLevelService.findPredefinedPromotionLevelByName(name)?.id()
                    )
                }
            }
        }
    }

    @Test
    fun `Updating a predefined promotion level`() {
        asAdmin {
            val ppl = predefinedPromotionLevel()
            val name = uid("ppl_")
            run(
                """
                    mutation {
                        updatePredefinedPromotionLevel(input: {
                            id: ${ppl.id},
                            name: "$name",
                            description: "Some text"
                        }) {
                            predefinedPromotionLevel {
                                id
                            }
                            errors {
                                message
                            }
                        }
                    }
                """
            ) { data ->
                checkGraphQLUserErrors(data, "updatePredefinedPromotionLevel") { node ->
                    val id = node.path("predefinedPromotionLevel").path("id").asInt()
                    assertEquals(ppl.id(), id)
                    assertEquals(
                        name,
                        predefinedPromotionLevelService.getPredefinedPromotionLevel(ID.of(id)).name
                    )
                    assertEquals(
                        id,
                        predefinedPromotionLevelService.findPredefinedPromotionLevelByName(name)?.id()
                    )
                }
            }
        }
    }

    @Test
    fun `Deleting a predefined promotion level`() {
        asAdmin {
            val ppl = predefinedPromotionLevel()
            run(
                """
                    mutation {
                        deletePredefinedPromotionLevel(input: {
                            id: ${ppl.id}
                        }) {
                            errors {
                                message
                            }
                        }
                    }
                """
            ) { data ->
                checkGraphQLUserErrors(data, "deletePredefinedPromotionLevel")
                assertEquals(
                    null,
                    predefinedPromotionLevelService.findPredefinedPromotionLevelByName(ppl.name)
                )
            }
        }
    }

}