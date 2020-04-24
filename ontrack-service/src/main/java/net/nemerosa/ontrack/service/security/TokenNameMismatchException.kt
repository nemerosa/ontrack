package net.nemerosa.ontrack.service.security

import org.springframework.security.core.AuthenticationException

class TokenNameMismatchException : AuthenticationException(
        "The name sent together with the token does not match the name of the account associated with the token."
)