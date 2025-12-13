package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.model.security.PermissionInput
import net.nemerosa.ontrack.model.security.PermissionTargetType
import net.nemerosa.ontrack.model.security.Roles
import org.junit.jupiter.api.Test
import kotlin.jvm.optionals.getOrNull
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AccountPermissionsMutationsIT : AbstractQLKTITSupport() {

    @Test
    @AsAdminTest
    fun `Adding a global permission to an account`() {
        val account = doCreateAccount()
        run(
            """
                mutation {
                    grantGlobalRoleToAccount(input: {
                        accountId: ${account.id},
                        globalRole: "${Roles.GLOBAL_AUTOMATION}"
                    }) {
                        errors {
                            message
                        }
                    }
                }
            """.trimIndent()
        )
        val globalRole = accountService.getGlobalRoleForAccount(account).getOrNull()
        assertEquals(Roles.GLOBAL_AUTOMATION, globalRole?.id)
    }

    @Test
    @AsAdminTest
    fun `Adding a global permission to an account group`() {
        val accountGroup = doCreateAccountGroup()
        run(
            """
                mutation {
                    grantGlobalRoleToAccountGroup(input: {
                        accountGroupId: ${accountGroup.id},
                        globalRole: "${Roles.GLOBAL_READ_ONLY}"
                    }) {
                        errors {
                            message
                        }
                    }
                }
            """.trimIndent()
        )
        val globalRole = accountService.getGlobalRoleForAccountGroup(accountGroup).getOrNull()
        assertEquals(Roles.GLOBAL_READ_ONLY, globalRole?.id)
    }

    @Test
    @AsAdminTest
    fun `Deleting a global permission from an account`() {
        val account = doCreateAccount()
        accountService.saveGlobalPermission(
            PermissionTargetType.ACCOUNT,
            account.id(),
            PermissionInput(role = Roles.GLOBAL_AUTOMATION)
        )
        run(
            """
                mutation {
                    deleteGlobalRoleFromAccount(input: {
                        accountId: ${account.id}
                    }) {
                        errors {
                            message
                        }
                    }
                }
            """.trimIndent()
        )
        val globalRole = accountService.getGlobalRoleForAccount(account).getOrNull()
        assertNull(globalRole?.id)
    }

    @Test
    @AsAdminTest
    fun `Deleting a global permission from an account group`() {
        val accountGroup = doCreateAccountGroup()
        accountService.saveGlobalPermission(
            PermissionTargetType.GROUP,
            accountGroup.id(),
            PermissionInput(role = Roles.GLOBAL_READ_ONLY)
        )
        run(
            """
                mutation {
                    deleteGlobalRoleFromAccountGroup(input: {
                        accountGroupId: ${accountGroup.id}
                    }) {
                        errors {
                            message
                        }
                    }
                }
            """.trimIndent()
        )
        val globalRole = accountService.getGlobalRoleForAccountGroup(accountGroup).getOrNull()
        assertNull(globalRole?.id)
    }

}