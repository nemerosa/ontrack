package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.security.GroupMappingService
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GroupMappingServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var groupMappingService: GroupMappingService

    @Test
    fun `Unmapped group`() {
        assertNull(
            groupMappingService.getMappedGroup(uid("any-")),
            "Unmapped group"
        )
    }

    @Test
    fun `Mapped group`() {
        val idpGroup = uid("any-")
        val group = asAdmin {
            val group = doCreateAccountGroup()
            groupMappingService.mapGroup(idpGroup, group)
            group
        }
        assertEquals(
            group.id(),
            groupMappingService.getMappedGroup(idpGroup)?.id(),
            "Mapped group"
        )
    }

    @Test
    fun `Resilience to leading slash in IdP group`() {
        val idpGroup = "/" + uid("any-")
        val group = asAdmin {
            val group = doCreateAccountGroup()
            groupMappingService.mapGroup(idpGroup, group)
            group
        }
        assertEquals(
            group.id(),
            groupMappingService.getMappedGroup(idpGroup)?.id(),
            "Mapped group"
        )
    }

}