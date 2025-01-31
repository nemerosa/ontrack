package net.nemerosa.ontrack.model.security

import org.springframework.security.core.context.SecurityContextHolder

/**
 * Given the current Spring security context, returns the corresponding [UserContext].
 */
fun UserContextService.currentSpringSecurityContextToUserContext() =
    springSecurityContextToUserContext(
        securityContext = SecurityContextHolder.getContext()
    )
