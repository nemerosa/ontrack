package net.nemerosa.ontrack.extension.ldap

import net.nemerosa.ontrack.model.security.AuthenticationSource.Companion.of
import net.nemerosa.ontrack.model.security.AuthenticationSourceProvider
import org.springframework.stereotype.Component

@Component
class LDAPAuthenticationSourceProvider : AuthenticationSourceProvider {

    override val source = of(
            LDAP_AUTHENTICATION_SOURCE,
            "LDAP authentication"
    )

    companion object {
        const val LDAP_AUTHENTICATION_SOURCE = "ldap"
    }
}