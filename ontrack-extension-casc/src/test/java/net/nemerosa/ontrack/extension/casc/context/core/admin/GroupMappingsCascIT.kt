package net.nemerosa.ontrack.extension.casc.context.core.admin

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.model.security.AccountGroupInput
import net.nemerosa.ontrack.model.security.GroupMappingService
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@AsAdminTest
class GroupMappingsCascIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var groupMappingService: GroupMappingService

    @Test
    fun `Adding mappings and groups using Casc`() {
        val groupName = uid("g-")
        val idpGroupName = uid("idp-")
        casc(
            """
                ontrack:
                    admin:
                        groups:
                          - name: $groupName
                            description: Test group
                        group-mappings:
                           - idp: $idpGroupName
                             group: $groupName
            """.trimIndent()
        )

        assertNotNull(
            accountService.findAccountGroupByName(groupName),
            "Group created"
        )

        assertEquals(
            groupName,
            groupMappingService.getMappedGroup(idpGroupName)?.name,
            "Group mapped"
        )
    }

    @Test
    fun `Casc mappings are authoritative`() {
        val groupNames = (1..3).map { uid("g-") }
        val idpGroupNames = (1..3).map { uid("idp-") }

        val group0 = accountService.createGroup(
            AccountGroupInput(
                groupNames[0],
                "Created programmatically"
            )
        )

        groupMappingService.mapGroup(idpGroupNames[0], group0)

        casc(
            """
                ontrack:
                    admin:
                        groups:
                          - name: ${groupNames[1]}
                            description: Test group 1
                          - name: ${groupNames[2]}
                            description: Test group 2
                        group-mappings:
                           - idp: ${idpGroupNames[1]}
                             group: ${groupNames[1]}
                           - idp: ${idpGroupNames[2]}
                             group: ${groupNames[2]}
            """.trimIndent()
        )

        assertEquals(
            null,
            groupMappingService.getMappedGroup(idpGroupNames[0])?.name,
            "Group 0 unmapped"
        )

        assertEquals(
            groupNames[1],
            groupMappingService.getMappedGroup(idpGroupNames[1])?.name,
            "Group 1 mapped"
        )

        assertEquals(
            groupNames[2],
            groupMappingService.getMappedGroup(idpGroupNames[2])?.name,
            "Group 2 mapped"
        )
    }

}