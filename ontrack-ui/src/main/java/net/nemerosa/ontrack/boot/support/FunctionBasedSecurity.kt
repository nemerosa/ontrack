package net.nemerosa.ontrack.boot.support

import net.nemerosa.ontrack.model.security.ApplicationManagement
import net.nemerosa.ontrack.model.security.OntrackAuthenticatedUser
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class FunctionBasedSecurity {

    /**
     * Used in Spring EL expressions.
     */
    @Suppress("unused")
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
            check: (OntrackAuthenticatedUser) -> Boolean
    ): Boolean {
        val details = authentication.principal
        return if (details is OntrackAuthenticatedUser) {
            check(details)
        } else {
            false
        }
    }

}