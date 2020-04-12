package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.repository.AccountRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.dao.DaoAuthenticationProvider

@Configuration
class SecurityConfiguration(
        private val accountRepository: AccountRepository
) {

    @Bean
    fun builtinAuthenticationProvider() = DaoAuthenticationProvider().apply {
        setUserDetailsService(BuiltinUserDetailsService(accountRepository))
    }

}