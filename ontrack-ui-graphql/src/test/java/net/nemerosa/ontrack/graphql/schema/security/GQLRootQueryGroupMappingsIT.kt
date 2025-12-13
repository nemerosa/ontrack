package net.nemerosa.ontrack.graphql.schema.security

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.security.GroupMappingService
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class GQLRootQueryGroupMappingsIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var groupMappingService: GroupMappingService

    @Test
    @AsAdminTest
    fun `List of group mappings`() {
        val idpGroup = uid("/test-")
        val group = doCreateAccountGroup()
        groupMappingService.mapGroup(idpGroup, group)
        run(
            """
                {
                    groupMappings {
                        idpGroup
                        group {
                            id
                        }
                    }
                }
            """.trimIndent()
        ) { data ->
            assertEquals(
                listOf(
                    mapOf(
                        "idpGroup" to idpGroup,
                        "group" to mapOf("id" to group.id().toString())
                    )
                ).asJson(),
                data.path("groupMappings"),
            )
        }
    }

}