package net.nemerosa.ontrack.model.security

import org.springframework.security.core.userdetails.UserDetails

/**
 * Representation of an authenticated user, its details and its complete granted ACL.
 */
interface OntrackAuthenticatedUser : UserDetails {

    /**
     * Associated user
     */
    val user: OntrackUser

    /**
     * Associated account
     */
    val account: Account

    /**
     * Checks if the [fn] global function is granted to this user
     */
    fun isGranted(fn: Class<out GlobalFunction>): Boolean

    /**
     * Checks if the [fn] project function is granted to this user on the project identified by [projectId].
     */
    fun isGranted(projectId: Int, fn: Class<out ProjectFunction>): Boolean

    /**
     * Account ID
     */
    fun id(): Int = account.id()

}
