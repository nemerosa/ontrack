package net.nemerosa.ontrack.model.security

import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.Signature
import java.util.*

interface SecurityService {

    fun checkGlobalFunction(fn: Class<out GlobalFunction>)
    fun isGlobalFunctionGranted(fn: Class<out GlobalFunction>): Boolean

    fun checkProjectFunction(projectId: Int, fn: Class<out ProjectFunction>)
    fun checkProjectFunction(entity: ProjectEntity, fn: Class<out ProjectFunction>) {
        checkProjectFunction(entity.projectId(), fn)
    }

    fun isProjectFunctionGranted(projectId: Int, fn: Class<out ProjectFunction>): Boolean
    fun isProjectFunctionGranted(entity: ProjectEntity, fn: Class<out ProjectFunction>): Boolean {
        return isProjectFunctionGranted(entity.projectId(), fn)
    }

    /**
     * Returns the current logged account or `null` if none is logged.
     */
    val currentAccount: OntrackAuthenticatedUser?

    /**
     * Is the current user logged?
     */
    val isLogged: Boolean
        get() = currentAccount != null

    /**
     * Returns the current logged account as an option
     *
     */
    @get:Deprecated("Use getCurrentAccount directly and check for null")
    val account: Optional<OntrackAuthenticatedUser>
        get() = Optional.ofNullable(currentAccount)

    val currentSignature: Signature

    /**
     * Performs a call as admin. This is mainly by internal code to access parts of the application
     * which are usually protected from the current context, but that need to be accessed locally.
     *
     * @param supplier Call to perform in a protected context
     * @param <T>      Type of data to get back
     * @return A new supplier running in a new security context
    </T> */
    fun <T> runAsAdmin(supplier: () -> T): () -> T

    fun <T> asAdmin(supplier: () -> T): T

    // For Java compatibility
    fun asAdmin(code: Runnable): Unit = asAdmin {
        code.run()
    }


    /**
     * In some asynchronous operations, we need to run a task with the same credentials that initiated the operation.
     * This method creates a wrapping supplier that holds the initial security context.
     *
     * @param fn  Call to perform in a protected context
     * @param <T> Type of data to get back
     * @return A new function running in a new security context
     */
    fun <T, R> runner(fn: (T) -> R): (T) -> R
}