package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.security.*
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AccountServiceIT : AbstractDSLTestSupport() {

    @Test
    fun `Account groups can be created with any character`() {
        asAdmin {
            val id = accountService.createGroup(AccountGroupInput(
                    name = "Nom accentué",
                    description = "Un nom avec des accents",
                    autoJoin = false
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