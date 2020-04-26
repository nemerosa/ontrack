package net.nemerosa.ontrack.extension.ldap

import org.springframework.security.core.AuthenticationException

class LDAPNotALDAPAccountException(username: String) : AuthenticationException(
        "Account with name $username is not a LDAP account."
)