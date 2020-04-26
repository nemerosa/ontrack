package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.exceptions.AuthenticationSourceProviderNotFoundException
import net.nemerosa.ontrack.model.security.AuthenticationSourceProvider
import net.nemerosa.ontrack.model.security.AuthenticationSourceService
import org.springframework.stereotype.Service

@Service
class AuthenticationSourceServiceImpl(providers: Collection<AuthenticationSourceProvider>) : AuthenticationSourceService {

    private val providers: Map<String, AuthenticationSourceProvider> = providers.associateBy {
        it.source.id
    }

    override fun getAuthenticationSourceProvider(mode: String): AuthenticationSourceProvider {
        return providers[mode] ?: throw AuthenticationSourceProviderNotFoundException(mode)
    }

}
