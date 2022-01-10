package net.nemerosa.ontrack.graphql

import org.junit.Test
import kotlin.test.assertTrue

class EntityCountsGraphQLIT: AbstractQLKTITJUnit4Support() {

    @Test
    fun `Project counts`() {
        asAdmin {
            project()
            run("""{
                entityCounts {
                    projects
                }
            }""").let { data ->
                assertTrue(
                    data.path("entityCounts").path("projects").asInt() > 0,
                    "At least one project"
                )
            }
        }
    }

}