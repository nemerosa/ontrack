package net.nemerosa.ontrack.model.security

import org.springframework.security.core.userdetails.UserDetails

/**
 * Representation of an authenticated user, its details.
 */
interface OntrackUser : UserDetails {
    /**
     * Associated account ID
     */
    val accountId: Int

}
