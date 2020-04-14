package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.GlobalFunction
import net.nemerosa.ontrack.model.security.OntrackAuthenticatedUser
import net.nemerosa.ontrack.model.security.ProjectFunction
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.Signature
import net.nemerosa.ontrack.model.structure.Signature.Companion.anonymous
import net.nemerosa.ontrack.model.structure.Signature.Companion.of
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.stereotype.Component

@Component
class SecurityServiceImpl : SecurityService {

    override fun checkGlobalFunction(fn: Class<out GlobalFunction>) {
        if (!isGlobalFunctionGranted(fn)) {
            throw AccessDeniedException("Global function '${fn.simpleName}' is not granted.")
        }
    }

    override fun isGlobalFunctionGranted(fn: Class<out GlobalFunction>): Boolean {
        // Gets the user
        val user = currentAccount
        // Checks
        return user != null && user.isGranted(fn)
    }

    override fun checkProjectFunction(projectId: Int, fn: Class<out ProjectFunction>) {
        if (!isProjectFunctionGranted(projectId, fn)) {
            throw AccessDeniedException(String.format("Project function '%s' is not granted", fn.simpleName))
        }
    }

    override fun isProjectFunctionGranted(projectId: Int, fn: Class<out ProjectFunction>): Boolean {
        // Gets the user
        val user = currentAccount
        // Checks
        return user != null && user.isGranted(projectId, fn)
    }

    override val currentAccount: OntrackAuthenticatedUser?
        get() {
            val context = SecurityContextHolder.getContext()
            val authentication = context.authentication
            return if (authentication != null && authentication.isAuthenticated && authentication.principal is OntrackAuthenticatedUser) {
                authentication.principal as OntrackAuthenticatedUser
            } else {
                null
            }
        }

    override val currentSignature: Signature
        get() {
            val authenticatedUser = currentAccount
            return if (authenticatedUser != null) {
                of(authenticatedUser.account.name)
            } else {
                anonymous()
            }
        }

    override fun <T> runAsAdmin(supplier: () -> T): () -> T {
        // Gets the current account (if any)
        val account = currentAccount
        // Creates a temporary admin context
        val adminContext = SecurityContextImpl()
        adminContext.authentication = RunAsAdminAuthentication(account)
        // Returns a callable that sets the context before running the target callable
        return withSecurityContext(supplier, adminContext)
    }

    override fun <T> asAdmin(supplier: () -> T): T = runAsAdmin(supplier)()

    override fun <T, R> runner(fn: (T) -> R): (T) -> R {
        // Current context
        val context = SecurityContextHolder.getContext()
        // Uses it
        return withSecurityContext(fn, context)
    }

    private fun <T, R> withSecurityContext(fn: (T) -> R, context: SecurityContext): (T) -> R {
        return { input: T ->
            val oldContext = SecurityContextHolder.getContext()
            try {
                SecurityContextHolder.setContext(context)
                // Result
                fn(input)
            } finally {
                SecurityContextHolder.setContext(oldContext)
            }
        }
    }

    protected fun <T> withSecurityContext(supplier: () -> T, context: SecurityContext?): () -> T {
        // Returns a callable that sets the context before running the target callable
        return {
            val oldContext = SecurityContextHolder.getContext()
            try {
                SecurityContextHolder.setContext(context)
                // Result
                supplier()
            } finally {
                SecurityContextHolder.setContext(oldContext)
            }
        }
    }
}