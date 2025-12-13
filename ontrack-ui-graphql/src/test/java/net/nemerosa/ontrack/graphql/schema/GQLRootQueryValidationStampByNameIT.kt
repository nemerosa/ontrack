package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.it.AsAdminTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@AsAdminTest
class GQLRootQueryValidationStampByNameIT : AbstractQLKTITSupport() {

    @Test
    fun `Validation stamp by name`() {
        project {
            branch {
                val vs = validationStamp()
                run(
                    """
                        {
                            validationStampByName(
                                project: "${vs.project.name}",
                                branch: "${vs.branch.name}",
                                name: "${vs.name}",
                            ) {
                                id
                            }
                        }
                    """.trimIndent()
                ) { data ->
                    val id = data.path("validationStampByName")
                        .path("id").asInt()
                    assertEquals(vs.id(), id)
                }
            }
        }
    }

}