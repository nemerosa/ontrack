package net.nemerosa.ontrack.extension.casc.context.core.admin

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.model.security.AccountGroupInput
import net.nemerosa.ontrack.model.security.PermissionInput
import net.nemerosa.ontrack.model.security.PermissionTargetType
import net.nemerosa.ontrack.model.security.Roles
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class AccountGroupGlobalPermissionsAdminContextIT : AbstractCascTestSupport() {

    @Test
    fun `Adding new group roles`() {
        val name = uid("g")
        asAdmin {
            val group = accountService.createGroup(AccountGroupInput(name, "Group $name"))
            casc("""
                ontrack:
                    admin:
                        group-permissions:
                            - group: $name
                              role: AUTOMATION
            """.trimIndent())
            val role = accountService.getGlobalRoleForAccountGroup(group).getOrNull()
            assertEquals(Roles.GLOBAL_AUTOMATION, role?.id)
        }
    }

    @Test
    fun `Updating existing group roles`() {
        val name = uid("g")
        asAdmin {
            val group = accountService.createGroup(AccountGroupInput(name, "Group $name"))
            accountService.saveGlobalPermission(PermissionTargetType.GROUP,
                group.id(),
                PermissionInput(Roles.GLOBAL_ADMINISTRATOR))
            casc("""
                ontrack:
                    admin:
                        group-permissions:
                            - group: $name
                              role: AUTOMATION
            """.trimIndent())
            val role = accountService.getGlobalRoleForAccountGroup(group).getOrNull()
            assertEquals(Roles.GLOBAL_AUTOMATION, role?.id)
        }
    }

    @Test
    fun `Preserving existing group roles`() {
        val existing = uid("g")
        val name = uid("g")
        asAdmin {
            val existingGroup = accountService.createGroup(AccountGroupInput(existing, "Group $existing"))
            val group = accountService.createGroup(AccountGroupInput(name, "Group $name"))
            accountService.saveGlobalPermission(PermissionTargetType.GROUP,
                existingGroup.id(),
                PermissionInput(Roles.GLOBAL_ADMINISTRATOR))
            casc("""
                ontrack:
                    admin:
                        group-permissions:
                            - group: $name
                              role: AUTOMATION
            """.trimIndent())
            // New role added
            assertEquals(Roles.GLOBAL_AUTOMATION,
                accountService.getGlobalRoleForAccountGroup(group).getOrNull()?.id)
            // Old mapping preserved
            assertEquals(Roles.GLOBAL_ADMINISTRATOR,
                accountService.getGlobalRoleForAccountGroup(existingGroup).getOrNull()?.id)
        }
    }

}