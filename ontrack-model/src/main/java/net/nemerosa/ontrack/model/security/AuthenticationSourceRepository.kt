package net.nemerosa.ontrack.model.security

import net.nemerosa.ontrack.model.exceptions.AuthenticationSourceNotFoundException

interface AuthenticationSourceRepository {

    /**
     * List of all authentication sources
     */
    val authenticationSources: List<AuthenticationSource>

    /**
     * List of all authentication source providers
     */
    val authenticationSourceProviders: List<AuthenticationSourceProvider>

    /**
     * Gets an authentication source provider using its ID.
     */
    fun getAuthenticationSourceProvider(provider: String): AuthenticationSourceProvider

    /**
     * Gets an authentication source using the provider ID and the source name.
     */
    fun getAuthenticationSource(provider: String, source: String): AuthenticationSource? =
            getAuthenticationSourceProvider(provider)
                    .sources
                    .find { it.key == source }

    /**
     * Gets an authentication source using the provider ID and the source name.
     */
    fun getRequiredAuthenticationSource(provider: String, source: String): AuthenticationSource =
            getAuthenticationSource(provider, source)
                    ?: throw AuthenticationSourceNotFoundException(provider, source)

}