package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.exceptions.AuthenticationSourceProviderNotFoundException
import net.nemerosa.ontrack.model.security.AuthenticationSource
import net.nemerosa.ontrack.model.security.AuthenticationSourceProvider
import net.nemerosa.ontrack.model.security.AuthenticationSourceService
import org.springframework.stereotype.Service

@Service
class AuthenticationSourceServiceImpl(providers: Collection<AuthenticationSourceProvider>) : AuthenticationSourceService {

    private val providers: Map<String, AuthenticationSourceProvider> = providers.associateBy {
        it.id
    }

    override val authenticationSourceProviders: List<AuthenticationSourceProvider>
        get() = providers.values.sortedBy { it.id }

    override val authenticationSources: List<AuthenticationSource>
        get() = providers.values.flatMap { it.sources }.sortedBy { it.id }

    override fun getAuthenticationSourceProvider(id: String): AuthenticationSourceProvider {
        return providers[id] ?: throw AuthenticationSourceProviderNotFoundException(id)
    }

}
