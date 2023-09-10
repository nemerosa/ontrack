package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.json.getRequiredIntField
import net.nemerosa.ontrack.test.TestUtils
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GQLProjectLastBuildsWithPromotionsFieldContributorIT : AbstractQLKTITSupport() {

    @Test
    fun `Getting last promotion run for a promotion in a project`() {
        val plName = TestUtils.uid("pl_")
        asAdmin {
            project {
                branch {
                    val branch1 = this
                    val pl1 = promotionLevel(name = plName)
                    build {
                        promote(pl1)
                    }
                    branch {
                        val pl2 = promotionLevel(name = plName)
                        build {
                            promote(pl2)
                        }
                        // Promotes a new build in (1)
                        branch1.apply {
                            val build1 = build {
                                promote(pl1)
                            }
                            // Looking for the last promotion run
                            run(
                                """
                                    {
                                        projects(id: ${project.id()}) {
                                            lastBuildsWithPromotions(promotions: ["${plName}"]) {
                                                build {
                                                    id
                                                }
                                                promotionLevel {
                                                    id
                                                }
                                            }
                                        }
                                    }
                                """
                            ) { data ->
                                val runs = data.path("projects").path(0).path("lastBuildsWithPromotions")
                                assertNotNull(runs.firstOrNull(), "Found one run") { run ->
                                    assertEquals(build1.id(), run.path("build").getRequiredIntField("id"))
                                    assertEquals(pl1.id(), run.path("promotionLevel").getRequiredIntField("id"))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}