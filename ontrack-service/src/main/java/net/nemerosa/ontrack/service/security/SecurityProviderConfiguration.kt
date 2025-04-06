package net.nemerosa.ontrack.service.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.DelegatingPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder


@Configuration
class SecurityProviderConfiguration {

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