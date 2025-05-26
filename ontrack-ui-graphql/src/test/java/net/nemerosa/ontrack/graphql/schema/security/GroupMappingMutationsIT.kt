package net.nemerosa.ontrack.graphql.schema.security

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.model.security.GroupMappingService
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

@AsAdminTest
class GroupMappingMutationsIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var groupMappingService: GroupMappingService

    @Test
    fun `Creating a mapping`() {
        val idpGroup = uid("/test-")
        val group = doCreateAccountGroup()
        run(
            """
                mutation {
                    mapGroup(input: {
                        idpGroup: "$idpGroup",
                        groupId: ${group.id()}
                    }) {
                        errors {
                            message
                        }
                    }
                }
            """.trimIndent()
        )
        assertEquals(group.id(), groupMappingService.getMappedGroup(idpGroup)?.id())
    }

    @Test
    fun `Updating a mapping`() {
        val idpGroup = uid("/test-")
        val group = doCreateAccountGroup()
        val otherGroup = doCreateAccountGroup()
        groupMappingService.mapGroup(idpGroup, group)
        run(
            """
                mutation {
                    mapGroup(input: {
                        idpGroup: "$idpGroup",
                        groupId: ${otherGroup.id()}
                    }) {
                        errors {
                            message
                        }
                    }
                }
            """.trimIndent()
        )
        assertEquals(otherGroup.id(), groupMappingService.getMappedGroup(idpGroup)?.id())
    }

    @Test
    fun `Deleting a mapping`() {
        val idpGroup = uid("/test-")
        val group = doCreateAccountGroup()
        groupMappingService.mapGroup(idpGroup, group)
        run(
            """
                mutation {
                    mapGroup(input: {
                        idpGroup: "$idpGroup",
                        groupId: null
                    }) {
                        errors {
                            message
                        }
                    }
                }
            """.trimIndent()
        )
        assertEquals(null, groupMappingService.getMappedGroup(idpGroup)?.id())
    }

}