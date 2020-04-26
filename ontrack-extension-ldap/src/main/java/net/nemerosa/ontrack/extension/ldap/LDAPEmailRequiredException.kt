package net.nemerosa.ontrack.extension.ldap

import org.springframework.security.core.AuthenticationException

class LDAPEmailRequiredException : AuthenticationException(
        "Email must be provided by the LDAP."
)