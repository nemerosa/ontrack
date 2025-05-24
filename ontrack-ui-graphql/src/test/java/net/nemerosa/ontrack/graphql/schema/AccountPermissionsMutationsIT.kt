package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.model.security.Roles
import org.junit.jupiter.api.Test
import kotlin.jvm.optionals.getOrNull
import kotlin.test.assertEquals

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

}