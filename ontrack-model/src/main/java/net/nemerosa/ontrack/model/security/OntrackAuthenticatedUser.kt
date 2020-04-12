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

}
