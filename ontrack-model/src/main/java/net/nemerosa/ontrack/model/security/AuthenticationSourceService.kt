package net.nemerosa.ontrack.model.security

interface AuthenticationSourceService {

    fun getAuthenticationSourceProvider(mode: String): AuthenticationSourceProvider

    fun getAuthenticationSource(mode: String): AuthenticationSource = getAuthenticationSourceProvider(mode).source

}