package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.test.assertJsonNull
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@AsAdminTest
class GQLRootQueryBranchByNameIT : AbstractQLKTITSupport() {

    @Test
    fun `Branch found`() {
        project {
            branch {
                run(
                    """
                        {
                            branchByName(project: "${project.name}", name: "$name") {
                                id
                            }
                        }
                    """.trimIndent()
                ) { data ->
                    assertEquals(
                        id(),
                        data.path("branchByName").path("id").asInt()
                    )
                }
            }
        }
    }

    @Test
    fun `Branch not found`() {
        project {
            run(
                """
                    {
                        branchByName(project: "${project.name}", name: "xxx") {
                            id
                        }
                    }
                """.trimIndent()
            ) { data ->
                assertJsonNull(data.path("branchByName"))
            }
        }
    }
}
