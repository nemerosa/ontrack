package net.nemerosa.ontrack.model.security

import org.springframework.security.core.context.SecurityContext

/**
 * Service used to convert the Spring security context into an [UserContext].
 */
interface UserContextService {

    /**
     * Given a Spring security context, returns the corresponding [UserContext].
     */
    fun springSecurityContextToUserContext(securityContext: SecurityContext): UserContext

}