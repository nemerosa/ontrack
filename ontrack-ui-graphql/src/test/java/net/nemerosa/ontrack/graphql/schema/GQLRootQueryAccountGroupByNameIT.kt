package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.it.AsAdminTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GQLRootQueryAccountGroupByNameIT : AbstractQLKTITSupport() {

    @Test
    @AsAdminTest
    fun `Getting a group by its name`() {
        val group = doCreateAccountGroup()
        run(
            """
                {
                    accountGroupByName(name: "${group.name}") {
                        id
                    }
                }
            """.trimIndent()
        ) { data ->
            assertEquals(
                group.id(),
                data.path("accountGroupByName").path("id").asInt()
            )
        }
    }

}