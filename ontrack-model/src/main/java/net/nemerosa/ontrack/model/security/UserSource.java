package net.nemerosa.ontrack.model.security;

import java.util.Optional;

/**
 * Loading user details from a source
 */
public interface UserSource {

    Optional<AccountUserDetails> loadUser(String username);

    void onLogout(String username);
}
