package net.nemerosa.ontrack.boot.support

import net.nemerosa.ontrack.model.support.EnvService
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class WebSecurityConfig(
    private val envService: EnvService,
) {

    /**
     * Management end points are accessible on a separate port without any authentication needed
     */
    @Bean
    @Order(1)
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
    @Order(2)
    fun apiWebSecurity(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { requests ->
                requests
                    .anyRequest().authenticated()
            }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .httpBasic {}
        return http.build()
    }

}