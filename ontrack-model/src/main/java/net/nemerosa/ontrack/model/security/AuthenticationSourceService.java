package net.nemerosa.ontrack.model.security;

import net.nemerosa.ontrack.model.exceptions.AuthenticationSourceProviderNotFoundException;

public interface AuthenticationSourceService {

    AuthenticationSourceProvider getAuthenticationSourceProvider(String mode) throws AuthenticationSourceProviderNotFoundException;

    default AuthenticationSource getAuthenticationSource(String mode) throws AuthenticationSourceProviderNotFoundException {
        return getAuthenticationSourceProvider(mode).getSource();
    }

}
