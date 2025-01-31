package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.*
import org.springframework.security.core.context.SecurityContext
import org.springframework.stereotype.Service
import kotlin.reflect.KClass

@Service
class UserContextServiceImpl : UserContextService {

    override fun springSecurityContextToUserContext(securityContext: SecurityContext): UserContext {
        val authentication = securityContext.authentication
        return if (authentication == null) {
            throw NoUserContextException()
        } else if (!authentication.isAuthenticated) {
            throw NotAuthenticatedUserContextException()
        } else if (authentication.principal is OntrackAuthenticatedUser) {
            val user = authentication.principal as OntrackAuthenticatedUser
            DefaultUserContext(
                user = user,
            )
        } else {
            throw MismatchAuthenticatedUserContextException(authentication.principal)
        }
    }

    private class DefaultUserContext(
        private val user: OntrackAuthenticatedUser,
    ) : UserContext {

        override val id: Int = user.account.id()

        override val name: String = user.account.name
        override val email: String = user.account.email
        override val fullName: String = user.account.fullName

        override fun isGlobalFunctionGranted(fn: KClass<out GlobalFunction>): Boolean =
            user.isGranted(fn.java)

        override fun isProjectFunctionGranted(projectId: Int, fn: KClass<out ProjectFunction>): Boolean =
            user.isGranted(projectId, fn.java)

    }

}