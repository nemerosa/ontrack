package net.nemerosa.ontrack.model.security

interface AuthenticationSourceService {

    /**
     * List of all authentication sources
     */
    val authenticationSources: List<AuthenticationSource>

    /**
     * List of all authentication source providers
     */
    val authenticationSourceProviders: List<AuthenticationSourceProvider>

    fun getAuthenticationSourceProvider(mode: String): AuthenticationSourceProvider

    fun getAuthenticationSource(mode: String): AuthenticationSource = getAuthenticationSourceProvider(mode).source

}