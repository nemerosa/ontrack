package net.nemerosa.ontrack.model.security;

/**
 * Defines the provider for an authentication source.
 *
 * @see net.nemerosa.ontrack.model.security.AuthenticationSource
 */
public interface AuthenticationSourceProvider {

    /**
     * Gets the source descriptor
     */
    AuthenticationSource getSource();

}
