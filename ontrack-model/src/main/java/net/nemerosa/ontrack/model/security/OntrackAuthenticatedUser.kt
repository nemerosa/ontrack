package net.nemerosa.ontrack.model.security

import org.springframework.security.core.userdetails.UserDetails

/**
 * Representation of an authenticated user, its details and its complete granted ACL.
 */
interface OntrackAuthenticatedUser : UserDetails, AuthorisationsCheck {

    /**
     * Associated authenticated user, which is associated to the [account].
     */
    val user: OntrackUser

    /**
     * Associated Ontrack account
     */
    val account: Account

    /**
     * Account ID
     */
    fun id(): Int = account.id()

}
