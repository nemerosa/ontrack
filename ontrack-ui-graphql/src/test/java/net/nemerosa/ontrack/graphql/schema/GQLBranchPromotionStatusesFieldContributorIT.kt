package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.it.AsAdminTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GQLBranchPromotionStatusesFieldContributorIT : AbstractQLKTITSupport() {

    @Test
    @AsAdminTest
    fun `Last promotion only is shown`() {
        project {
            branch {
                val pl = promotionLevel()
                build {
                    promote(pl)
                    val run2 = promote(pl)

                    run(
                        """
                                {
                                    branch(id: ${branch.id}) {
                                        promotionStatuses(names: ["${pl.name}"]) {
                                            id
                                        }
                                    }
                                }
                        """.trimIndent()
                    ) { data ->
                        val runId = data.path("branch")
                            .path("promotionStatuses").path(0)
                            .path("id").asInt()
                        assertEquals(run2.id(), runId)
                    }
                }
            }
        }
    }

}