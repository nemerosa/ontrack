package net.nemerosa.ontrack.repository.support

import net.nemerosa.ontrack.model.exceptions.AuthenticationSourceProviderNotFoundException
import net.nemerosa.ontrack.model.security.AuthenticationSource
import net.nemerosa.ontrack.model.security.AuthenticationSourceProvider
import net.nemerosa.ontrack.model.security.AuthenticationSourceRepository
import org.springframework.stereotype.Service

@Service
class AuthenticationSourceRepositoryImpl(providers: Collection<AuthenticationSourceProvider>) : AuthenticationSourceRepository {

    private val providers: Map<String, AuthenticationSourceProvider> = providers.associateBy {
        it.id
    }

    override val authenticationSourceProviders: List<AuthenticationSourceProvider>
        get() = providers.values.sortedBy { it.id }

    override val authenticationSources: List<AuthenticationSource>
        get() = providers.values.flatMap { it.sources }.sortedBy { it.key }

    override fun getAuthenticationSourceProvider(provider: String): AuthenticationSourceProvider {
        return providers[provider] ?: throw AuthenticationSourceProviderNotFoundException(provider)
    }

}
