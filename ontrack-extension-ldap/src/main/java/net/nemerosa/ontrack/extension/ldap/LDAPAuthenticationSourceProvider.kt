package net.nemerosa.ontrack.extension.ldap

import net.nemerosa.ontrack.model.security.AuthenticationSource
import net.nemerosa.ontrack.model.security.AuthenticationSourceProvider
import org.springframework.stereotype.Component

@Component
class LDAPAuthenticationSourceProvider : AuthenticationSourceProvider {

    override val source: AuthenticationSource = SOURCE

    companion object {
        val SOURCE = AuthenticationSource(
                id = "ldap",
                name = "LDAP authentication",
                isAllowingPasswordChange = false
        )
    }
}
