package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GQLRootQueryProjectIT : AbstractQLKTITSupport() {

    @Test
    fun `Getting a project using its ID`() {
        asAdmin {
            project {
                run(
                    """
                        {
                            project(id: $id) {
                                name
                            }
                        }
                    """
                ) { data ->
                    val name = data.path("project").path("name").asText()
                    assertEquals(this.name, name)
                }
            }
        }
    }

}