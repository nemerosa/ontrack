package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.AccountService
import net.nemerosa.ontrack.repository.AccountRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.DelegatingPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder


@Configuration
class SecurityConfiguration(
        private val accountService: AccountService,
        private val accountRepository: AccountRepository
) {

    @Bean
    fun builtinAuthenticationProvider() = DaoAuthenticationProvider().apply {
        setUserDetailsService(BuiltinUserDetailsService(accountService, accountRepository))
        setPasswordEncoder(passwordEncoder())
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder().apply {
        if (this is DelegatingPasswordEncoder) {
            /**
             * Legacy encoder
             */
            setDefaultPasswordEncoderForMatches(BCryptPasswordEncoder())
        }
    }

}