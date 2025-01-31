package net.nemerosa.ontrack.model.security

import kotlin.reflect.KClass

/**
 * Context linked to an authenticated user to be used throughout Ontrack services & processors.
 */
interface UserContext {

    /**
     * Technical ID of the user
     */
    val id: Int

    /**
     * Technical name for the user, unique inside a tenant.
     */
    val name: String

    /**
     * Email of the user
     */
    val email: String

    /**
     * Display name of the user
     */
    val fullName: String

    /**
     * Checks if the [fn] global function is granted for this user.
     */
    fun isGlobalFunctionGranted(fn: KClass<out GlobalFunction>): Boolean

    /**
     * Checks if the [fn] global function is granted for this user and this project.
     */
    fun isProjectFunctionGranted(projectId: Int, fn: KClass<out ProjectFunction>): Boolean

}