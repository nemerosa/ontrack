package net.nemerosa.ontrack.boot.support

import net.nemerosa.ontrack.model.security.Account
import net.nemerosa.ontrack.model.security.AccountUserDetails
import net.nemerosa.ontrack.model.security.ApplicationManagement
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class FunctionBasedSecurity {

    fun hasApplicationManagement(authentication: Authentication): Boolean {
        return hasGlobalFunction(authentication, ApplicationManagement::class)
    }

    private fun hasGlobalFunction(authentication: Authentication, fn: KClass<ApplicationManagement>): Boolean {
        return checkAccount(authentication) {
            it.isGranted(fn.java)
        }
    }

    private fun checkAccount(
            authentication: Authentication,
            check: (Account) -> Boolean
    ): Boolean {
        val details = authentication.principal
        return if (details is AccountUserDetails) {
            check(details.account)
        } else {
            false
        }
    }

}