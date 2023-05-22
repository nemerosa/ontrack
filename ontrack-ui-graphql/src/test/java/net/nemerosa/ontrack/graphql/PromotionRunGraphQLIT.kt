package net.nemerosa.ontrack.graphql

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PromotionRunGraphQLIT : AbstractQLKTITSupport() {

    @Test
    fun `Promotion run reference to build and validation stamp`() {
        project {
            branch {
                val pl = promotionLevel()
                build {
                    val run = promote(pl)
                    run("""{
                        promotionRuns(id: ${run.id}) {
                            build {
                                id
                            }
                            promotionLevel {
                                id
                                branch {
                                    id
                                    project {
                                        id
                                    }
                                }
                            }
                        }
                    }""") { data ->
                        val p = data.path("promotionRuns").first()
                        assertEquals(
                                id(),
                                p.path("build").path("id").asInt()
                        )
                        val plNode = p.path("promotionLevel")
                        assertEquals(pl.id(), plNode.path("id").asInt())
                        assertEquals(pl.project.id(), plNode.path("branch").path("project").path("id").asInt())
                    }
                }
            }
        }
    }

    @Test
    fun `Creating a promotion run where build is identified by ID`() {
        project {
            branch {
                val pl = promotionLevel()
                build {
                    val data = run("""
                        mutation CreatePromotionRun {
                            createPromotionRunById(input: {
                                buildId: $id,
                                promotion: "${pl.name}",
                                description: "My promotion"
                            }) {
                                promotionRun {
                                    id
                                    description
                                }
                                errors {
                                    message
                                }
                            }
                        }
                    """)
                    val node = assertNoUserError(data, "createPromotionRunById")
                    val run = node.path("promotionRun")
                    assertTrue(run.path("id").asInt() > 0, "Run created")
                    assertEquals("My promotion", run.path("description").asText())
                }
            }
        }
    }

    @Test
    fun `Creating a promotion run where build is identified by name`() {
        project {
            branch {
                val pl = promotionLevel()
                build {
                    val data = run("""
                        mutation CreatePromotionRun {
                            createPromotionRun(input: {
                                project: "${project.name}",
                                branch: "${branch.name}",
                                build: "$name",
                                promotion: "${pl.name}",
                                description: "My promotion"
                            }) {
                                promotionRun {
                                    id
                                    description
                                }
                                errors {
                                    message
                                }
                            }
                        }
                    """)
                    val node = assertNoUserError(data, "createPromotionRun")
                    val run = node.path("promotionRun")
                    assertTrue(run.path("id").asInt() > 0, "Run created")
                    assertEquals("My promotion", run.path("description").asText())
                }
            }
        }
    }

}