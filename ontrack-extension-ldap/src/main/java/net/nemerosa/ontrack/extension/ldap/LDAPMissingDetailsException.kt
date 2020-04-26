package net.nemerosa.ontrack.extension.ldap

import org.springframework.security.core.AuthenticationException

class LDAPMissingDetailsException : AuthenticationException(
        "Cannot get any information about the account from the LDAP."
)