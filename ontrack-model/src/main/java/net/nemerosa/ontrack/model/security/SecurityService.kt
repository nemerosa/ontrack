package net.nemerosa.ontrack.model.security

import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.Signature
import kotlin.reflect.KClass

interface SecurityService {

    /**
     * Checks that the current user is authenticated
     */
    fun checkAuthenticated()

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
     * List of [project functions][ProjectFunction] which are automatically assigned to authenticated users.
     */
    @Deprecated("Use AccountACLService")
    val autoProjectFunctions: Set<KClass<out ProjectFunction>>

    /**
     * List of [global functions][GlobalFunction] which are automatically assigned to authenticated users.
     */
    @Deprecated("Use AccountACLService")
    val autoGlobalFunctions: Set<KClass<out GlobalFunction>>

    /**
     * Returns the current logged account or `null` if none is logged.
     */
    @Deprecated("Use currentUser")
    val currentAccount: OntrackAuthenticatedUser?

    /**
     * Returns the current logged user or null if none is logged.
     */
    val currentUser: AuthenticatedUser?

    /**
     * Is the current user logged?
     */
    val isLogged: Boolean
        get() = currentAccount != null

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