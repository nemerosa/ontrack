package net.nemerosa.ontrack.extension.ldap

import net.nemerosa.ontrack.model.security.AuthenticationSource

object LDAPAuthenticationSource : AuthenticationSource(
        id = "ldap",
        name = "LDAP authentication",
        isAllowingPasswordChange = false
)
