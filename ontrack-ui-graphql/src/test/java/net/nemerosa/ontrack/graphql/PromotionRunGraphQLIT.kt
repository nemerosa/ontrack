package net.nemerosa.ontrack.graphql

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PromotionRunGraphQLIT : AbstractQLKTITSupport() {

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