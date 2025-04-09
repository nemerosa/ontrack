package net.nemerosa.ontrack.model.security

import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.support.PasswordChange

/**
 * Service to manage one's own account.
 */
interface UserService {
    /**
     * Changes his own password.
     */
    @Deprecated("Will be removed in V5")
    fun changePassword(input: PasswordChange): Ack
}