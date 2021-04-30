package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.security.*
import org.junit.Test
import org.springframework.security.access.AccessDeniedException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AccountServiceIT : AbstractDSLTestSupport() {

    @Test
    fun `Disabling an account`() {
        val account = doCreateAccount()
        assertFalse(account.disabled, "Accounts are enabled by default")
        asAdmin {
            accountService.setAccountDisabled(account.id, true)
            assertTrue(accountService.getAccount(account.id).disabled, "Account is disabled")
            accountService.setAccountDisabled(account.id, false)
            assertFalse(accountService.getAccount(account.id).disabled, "Account is enabled")
        }
    }

    @Test
    fun `Locking an account`() {
        val account = doCreateAccount()
        assertFalse(account.locked, "Accounts are unlocked by default")
        asAdmin {
            accountService.setAccountLocked(account.id, true)
            assertTrue(accountService.getAccount(account.id).locked, "Account is locked")
            accountService.setAccountLocked(account.id, false)
            assertFalse(accountService.getAccount(account.id).locked, "Account is unlocked")
        }
    }

    @Test
    fun `Disabling an account is granted to admin only`() {
        val account = doCreateAccount()
        asUserWith<AccountManagement> {
            accountService.setAccountDisabled(account.id, true)
        }
        asUser {
            assertFailsWith<AccessDeniedException> {
                accountService.setAccountDisabled(account.id, false)
            }
        }
        asAdmin {
            assertTrue(accountService.getAccount(account.id).disabled, "Account is disabled")
        }
    }

    @Test
    fun `Locking an account is granted to admin only`() {
        val account = doCreateAccount()
        asUserWith<AccountManagement> {
            accountService.setAccountLocked(account.id, true)
        }
        asUser {
            assertFailsWith<AccessDeniedException> {
                accountService.setAccountLocked(account.id, false)
            }
        }
        asAdmin {
            assertTrue(accountService.getAccount(account.id).locked, "Account is locked")
        }
    }

    @Test
    fun `Account groups can be created with any character`() {
        asAdmin {
            val id = accountService.createGroup(AccountGroupInput(
                    name = "Nom accentué",
                    description = "Un nom avec des accents"
            )).id
            // Loads the group again
            val group = accountService.getAccountGroup(id)
            assertEquals("Nom accentué", group.name)
        }
    }

    @Test
    fun account_with_no_role() {
        project {
            asUser().call {
                assertFalse(securityService.isGlobalFunctionGranted(AccountManagement::class.java), "As a normal user, must not have any global grant")
                assertFalse(securityService.isProjectFunctionGranted(project, BranchCreate::class.java), "As a normal user, must not have any project grant")
                assertFalse(securityService.isProjectFunctionGranted(project, ValidationRunCreate::class.java), "As a normal user, must not have any project grant")
                assertFalse(securityService.isProjectFunctionGranted(project, ValidationStampEdit::class.java), "As a normal user, must not have any project grant")
            }
        }
    }

    @Test
    fun account_with_global_role_controller() {
        project {
            asGlobalRole(Roles.GLOBAL_CONTROLLER) {
                assertFalse(securityService.isGlobalFunctionGranted(AccountManagement::class.java), "As a normal user, must not have any global grant")
                assertFalse(securityService.isProjectFunctionGranted(project, BranchCreate::class.java), "As a normal user, must not have any project grant")
                assertTrue(securityService.isProjectFunctionGranted(project, ValidationRunCreate::class.java), "As a controller, must have the right to create validation runs on any project")
                assertFalse(securityService.isProjectFunctionGranted(project, ValidationStampEdit::class.java), "As a normal user, must not have any project grant")
            }
        }
    }

    @Test
    fun account_with_global_role_controller_on_group() {
        // Setup
        val accountGroup = doCreateAccountGroupWithGlobalRole(Roles.GLOBAL_CONTROLLER)
        val account = doCreateAccount(accountGroup)

        // Checks
        project {
            asFixedAccount(account) {
                assertFalse(securityService.isGlobalFunctionGranted(AccountManagement::class.java), "As a normal user, must not have any global grant")
                assertFalse(securityService.isProjectFunctionGranted(project, BranchCreate::class.java), "As a normal user, must not have any project grant")
                assertTrue(securityService.isProjectFunctionGranted(project, ValidationRunCreate::class.java), "As a controller, must have the right to create validation runs on any project")
                assertFalse(securityService.isProjectFunctionGranted(project, ValidationStampEdit::class.java), "As a normal user, must not have any project grant")
            }
        }
    }

    /**
     * Regression test for #427
     */
    @Test
    fun admin_can_delete_promotion_run() {
        project {
            asAdmin {
                // Checks the account can delete a promotion run
                assertTrue(securityService.isProjectFunctionGranted(project, PromotionRunDelete::class.java), "An administrator must be granted the promotion run deletion")
            }
        }
    }
}