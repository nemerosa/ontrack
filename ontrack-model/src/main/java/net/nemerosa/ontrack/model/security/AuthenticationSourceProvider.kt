package net.nemerosa.ontrack.model.security

/**
 * Defines the provider for an authentication source.
 *
 * @see net.nemerosa.ontrack.model.security.AuthenticationSource
 */
interface AuthenticationSourceProvider {
    /**
     * Gets the source descriptor
     */
    val source: AuthenticationSource

    /**
     * Is this provided enabled?
     */
    val isEnabled: Boolean
}