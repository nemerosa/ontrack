package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.it.NoAuthTest
import net.nemerosa.ontrack.model.security.AccountAuthenticatedUser
import net.nemerosa.ontrack.model.security.AuthenticationStorageService
import net.nemerosa.ontrack.model.security.Roles
import net.nemerosa.ontrack.model.security.RunAsAuthenticatedUser
import net.nemerosa.ontrack.test.assertIs
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

@NoAuthTest
class AuthenticationStorageServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var authenticationStorageService: AuthenticationStorageService

    @Test
    fun `Storing and restoring the security context when there is no authentication`() {
        assertFailsWith<AuthenticationStorageServiceNoAuthException> {
            authenticationStorageService.getAccountId()
        }
    }

    @Test
    fun `Storing and restoring the security context for an account`() {
        asAccountWithGlobalRole(Roles.GLOBAL_ADMINISTRATOR) {
            val email = securityService.currentUser?.account?.email
            assertNotNull(email, "There is a current user with an email")
            val accountId = authenticationStorageService.getAccountId()
            assertTrue(accountId.isNotBlank())
            authenticationStorageService.withAccountId(accountId) {
                assertIs<AccountAuthenticatedUser>(securityService.currentUser) {
                    assertEquals(email, it.account.email)
                }
            }
        }
    }

    @Test
    fun `Storing and restoring the security context for a run-as admin`() {
        securityService.asAdmin {
            val accountId = authenticationStorageService.getAccountId()
            assertEquals(AuthenticationStorageService.RUN_AS_ADMINISTRATOR_ACCOUNT_ID, accountId)
            authenticationStorageService.withAccountId(accountId) {
                assertIs<RunAsAuthenticatedUser>(securityService.currentUser)
            }
        }
    }

}