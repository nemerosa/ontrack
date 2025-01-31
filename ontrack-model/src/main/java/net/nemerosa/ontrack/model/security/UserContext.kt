package net.nemerosa.ontrack.model.security

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

}