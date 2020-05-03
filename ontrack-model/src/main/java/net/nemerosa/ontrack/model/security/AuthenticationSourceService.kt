package net.nemerosa.ontrack.model.security

import net.nemerosa.ontrack.model.exceptions.AuthenticationSourceNotFoundException

interface AuthenticationSourceService {

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
    fun getAuthenticationSourceProvider(id: String): AuthenticationSourceProvider

    /**
     * Gets an authentication source using the provider ID and the source name.
     */
    fun getAuthenticationSource(id: String, name: String): AuthenticationSource =
            getAuthenticationSourceProvider(id)
                    .sources
                    .find { it.name == name }
                    ?: throw AuthenticationSourceNotFoundException(id, name)

}