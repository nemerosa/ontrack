package net.nemerosa.ontrack.model.security;

public interface AuthenticationSourceService {

    AuthenticationSourceProvider getAuthenticationSourceProvider(String mode);

    default AuthenticationSource getAuthenticationSource(String mode) {
        return getAuthenticationSourceProvider(mode).getSource();
    }

}
