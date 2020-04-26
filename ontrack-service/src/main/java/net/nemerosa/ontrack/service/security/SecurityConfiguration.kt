package net.nemerosa.ontrack.service.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.ProviderManager


@Configuration
class SecurityConfiguration(
        private val providers: List<AuthenticationProvider>
) {

    @Bean
    fun authenticationManager() = ProviderManager(providers)

}