package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.security.UserService
import net.nemerosa.ontrack.model.support.PasswordChange
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import kotlin.test.assertFailsWith

class UserServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var userService: UserService

    @Test
    fun `Locked account cannot change its password`() {
        val account = asAdmin {
            val initial = doCreateAccount()
            accountService.setAccountLocked(initial.id, true)
            accountService.getAccount(initial.id)
        }
        asFixedAccount(account) {
            assertFailsWith<AccessDeniedException> {
                userService.changePassword(PasswordChange("old", "new"))
            }
        }
    }

}