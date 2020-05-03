package net.nemerosa.ontrack.model.security

/**
 * Defines the provider for an authentication source.
 *
 * @see net.nemerosa.ontrack.model.security.AuthenticationSource
 */
interface AuthenticationSourceProvider {

    /**
     * ID of this provider
     */
    val id: String

    /**
     * Gets the sources from this provider
     */
    val sources: List<AuthenticationSource>
}