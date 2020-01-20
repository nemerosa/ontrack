package net.nemerosa.ontrack.extension.scm.catalog.contributors.acl

import com.fasterxml.jackson.databind.node.NullNode
import net.nemerosa.ontrack.extension.scm.catalog.CatalogFixtures
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.security.PermissionInput
import net.nemerosa.ontrack.model.security.PermissionTargetType
import net.nemerosa.ontrack.model.security.Roles
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ACLCatalogInfoContributorIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var contributor: ACLCatalogInfoContributor

    @Test
    fun `Dynamic contributor`() {
        assertTrue(contributor.isDynamic, "Dynamic contributor")
    }

    @Test
    fun `Project without any specific ACL returns an empty ACL catalog info`() {
        project {
            val info = contributor.fromStoredJson(this, NullNode.instance)
            assertNotNull(info) {
                assertTrue(info.projectRoles.isEmpty(), "No ACL")
            }
        }
    }

    @Test
    fun `Project with specific ACL`() {
        project {
            // Groups and roles
            val group = doCreateAccountGroup()
            val account1 = doCreateAccount()
            val account2 = doCreateAccount()
            // Assigns roles
            accountService.saveProjectPermission(id, PermissionTargetType.GROUP, group.id(), PermissionInput(Roles.PROJECT_PARTICIPANT))
            accountService.saveProjectPermission(id, PermissionTargetType.ACCOUNT, account1.id(), PermissionInput(Roles.PROJECT_PARTICIPANT))
            accountService.saveProjectPermission(id, PermissionTargetType.ACCOUNT, account2.id(), PermissionInput(Roles.PROJECT_OWNER))
            // Gets the info
            val info = contributor.fromStoredJson(this, NullNode.instance)
            assertNotNull(info) {
                val actual = info.projectRoles.flatMap { acl ->
                    acl.targets.map { target ->
                        "${acl.role.id}||${target.type.name}||${target.name}"
                    }
                }.toSet()
                val expected = setOf(
                        "PARTICIPANT||GROUP||${group.name}",
                        "PARTICIPANT||ACCOUNT||${account1.name}",
                        "OWNER||ACCOUNT||${account2.name}"
                )
                assertEquals(expected, actual)
                // Checks that the stored JSON is empty
                val json = contributor.asStoredJson(info)
                assertTrue(json.isNull, "No JSON for storage")
                // ... but for the client, everything is OK
                val client = contributor.asClientJson(info)
                assertFalse(client.isNull, "JSON for client")
            }
            // Checks that the collection of info never returns anything
            assertTrue(contributor.collectInfo(this, CatalogFixtures.entry()).projectRoles.isEmpty(), "No ACL on collection")
        }
    }

}