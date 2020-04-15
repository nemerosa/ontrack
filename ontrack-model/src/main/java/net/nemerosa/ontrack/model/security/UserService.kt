package net.nemerosa.ontrack.model.security;

import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.support.PasswordChange;

/**
 * Service to manage one's own account.
 */
public interface UserService {

    /**
     * Changes his own password.
     */
    Ack changePassword(PasswordChange input);

}
