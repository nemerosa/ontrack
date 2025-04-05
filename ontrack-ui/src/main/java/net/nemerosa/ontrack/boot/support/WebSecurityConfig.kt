package net.nemerosa.ontrack.boot.support

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@Configuration
class WebSecurityConfig {

    /**
     * Management end points are accessible on a separate port without any authentication needed
     */
    @Bean
    fun actuatorWebSecurity(http: HttpSecurity): SecurityFilterChain {
        http.securityMatcher(EndpointRequest.toAnyEndpoint())
            .authorizeHttpRequests { requests ->
                requests.anyRequest().permitAll()
            }
        return http.build()
    }

    /**
     * API login
     */
    @Bean
    fun apiWebSecurity(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { requests ->
                requests
                    .anyRequest().authenticated()
            }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt(Customizer.withDefaults())
            }
        return http.build()
    }

}